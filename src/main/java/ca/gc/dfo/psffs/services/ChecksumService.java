package ca.gc.dfo.psffs.services;

import ca.gc.dfo.psffs.domain.objects.ChecksumForObject;
import ca.gc.dfo.psffs.domain.objects.TripSetting;
import ca.gc.dfo.psffs.domain.objects.lookups.Vessel;
import ca.gc.dfo.psffs.domain.repositories.ChecksumForObjectRepository;
import ca.gc.dfo.psffs.domain.repositories.TripSettingRepository;
import ca.gc.dfo.psffs.domain.repositories.lookups.VesselRepository;
import ca.gc.dfo.psffs.json.ChecksumObjectResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChecksumService implements ApplicationContextAware
{
    public enum Objects
    {
        VESSELS(Vessel.class, VesselRepository.class,"findAllByOrderByOrderAsc"),
        TRIP_SETTINGS(TripSetting.class, TripSettingRepository.class, "findAll");

        Objects(Class<?> ENTITY, Class<?> REPO, String FINDER, Object... FINDER_ARGS)
        {
            this.ENTITY = ENTITY;
            this.REPO = REPO;
            this.FINDER = FINDER;
            this.FINDER_ARGS = FINDER_ARGS;
        }

        public Class<?> ENTITY;
        public Class<?> REPO;
        public String FINDER;
        public Object[] FINDER_ARGS;
    }

    @Autowired
    private ChecksumForObjectRepository repo;

    private ApplicationContext ctx;

    public ChecksumObjectResponse wrapChecksumAroundObjects(Class<?> entityClass, String currentChecksum)
    {
        ChecksumObjectResponse wrapper = new ChecksumObjectResponse();
        Objects object = getObjectEnumByClass(entityClass);
        wrapper.setChecksum(getChecksumForCTABByObject(object));
        ChecksumObjectResponse.Status status = currentChecksum != null && wrapper.getChecksum().getGeneratedUUID().equals(currentChecksum) ? ChecksumObjectResponse.Status.UP_TO_DATE : ChecksumObjectResponse.Status.OUT_OF_DATE;
        wrapper.setStatus(status);
        if (status.equals(ChecksumObjectResponse.Status.OUT_OF_DATE)) {
            wrapper.setObjects(executeFinder(object.ENTITY, object));
        }
        return wrapper;
    }

    public ChecksumForObject updateChecksumForObjectByEntityClass(Class<?> entityClass)
    {
        Objects object = getObjectEnumByClass(entityClass);
        ChecksumForObject checksum = null;
        if (object != null) {
            checksum = getChecksumForCTABByObject(object);
            checksum.setGeneratedUUID(UUID.randomUUID().toString());
            checksum = repo.save(checksum);
        }
        return checksum;
    }

    @Override
    public void setApplicationContext(ApplicationContext context)
    {
        this.ctx = context;
    }

    private ChecksumForObject getChecksumForCTABByObject(Objects object)
    {
        ChecksumForObject checksum = repo.findByObjectStoreNameEquals(object.name());
        if (checksum == null) {
            checksum = generateNewChecksum(object);
        }
        return checksum;
    }

    private ChecksumForObject generateNewChecksum(Objects object)
    {
        ChecksumForObject checksum = new ChecksumForObject();
        checksum.setObjectStoreName(object.name());
        checksum.setGeneratedUUID(UUID.randomUUID().toString());
        return repo.save(checksum);
    }

    private <T> T getRepositoryByObjectClass(Class<T> clazz)
    {
        boolean containsClass = false;
        for (Objects obj : Objects.values()) {
            if (obj.REPO.equals(clazz)) {
                containsClass = true;
                break;
            }
        }

        if (!containsClass) {
            throw new RuntimeException("The class that was passed into this method is not an Objects.REPO class");
        }

        return ctx.getBean(clazz);
    }

    private <E> List<E> executeFinder(Class<E> entityClass, Objects object)
    {
        Object repository = getRepositoryByObjectClass(object.REPO);
        Method finderMethod = null;
        Class<?>[] finderArgTypes = null;
        Object[] finderArgs = object.FINDER_ARGS;
        if (finderArgs != null && finderArgs.length > 0) {
            finderArgTypes = new Class<?>[finderArgs.length];
            for (int x = 0; x < finderArgs.length; x++) {
                finderArgTypes[x] = finderArgs[x].getClass();
            }
        }

        Class<?> classToEval = repository.getClass();
        while (!classToEval.equals(Object.class)) {
            try {
                if (finderArgTypes != null) {
                    finderMethod = classToEval.getDeclaredMethod(object.FINDER, finderArgTypes);
                } else {
                    finderMethod = classToEval.getDeclaredMethod(object.FINDER);
                }

                if (finderMethod != null) {
                    classToEval = Object.class;
                }
            } catch (NoSuchMethodException nsme) {
                classToEval = classToEval.getSuperclass();
            }
        }

        Object toReturn = null;
        if (finderMethod != null) {
            try {
                toReturn = finderMethod.invoke(repository, finderArgs);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("Could not find execute repository finder method: " + object.REPO.getSimpleName() + "." + finderMethod.getName());
            }
        }

        return toReturn != null && ((List<E>)toReturn).size() > 0 ? (List<E>) toReturn : new ArrayList<>();
    }

    public Objects getObjectEnumByClass(Class<?> entityClass)
    {
        Objects object = null;
        for (Objects obj : Objects.values()) {
            if (obj.ENTITY.equals(entityClass)) {
                object = obj;
                break;
            }
        }
        return object;
    }

    private static final Logger logger = LoggerFactory.getLogger(ChecksumService.class);
}
