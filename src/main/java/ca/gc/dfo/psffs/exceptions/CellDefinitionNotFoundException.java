package ca.gc.dfo.psffs.exceptions;

public class CellDefinitionNotFoundException extends RuntimeException
{
    public CellDefinitionNotFoundException(Integer year, Integer sampleSpeciesId)
    {
        super("No cell definition found for year [" + year + "]" +
                " and species id [" + sampleSpeciesId + "]");
    }
}
