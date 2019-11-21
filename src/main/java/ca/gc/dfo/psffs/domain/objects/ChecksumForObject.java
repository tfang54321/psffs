package ca.gc.dfo.psffs.domain.objects;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "PSFFS_CHECKSUM_FOR_OBJECT",
       indexes = @Index(columnList = "ctab_obj_store_name", name = "PSFFS_CFO_OBJ_STORE_NM_INDEX", unique = true))
@Data
public class ChecksumForObject
{
    @Id
    @GeneratedValue(generator = "checksumForObjectsSeqGen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "checksumForObjectsSeqGen", sequenceName = "PSFFS_CHKSUM_FOR_OBJECT_ID_SEQ", allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ctab_obj_store_name", unique = true)
    private String objectStoreName;

    @Column(name = "gen_uuid")
    private String generatedUUID;
}
