update PSFFS.SAMPLING_DATA set tag_nbr = null;
alter table PSFFS.SAMPLING_DATA
  modify tag_nbr varchar(10 char);