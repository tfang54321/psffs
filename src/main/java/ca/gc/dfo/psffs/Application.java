package ca.gc.dfo.psffs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = {
        "ca.gc.dfo.psffs", "ca.gc.dfo.spring_commons.commons_offline_wet"
})
@EnableTransactionManagement
public class Application
{
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
