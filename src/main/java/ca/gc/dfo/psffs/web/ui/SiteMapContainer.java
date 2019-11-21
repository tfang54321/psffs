package ca.gc.dfo.psffs.web.ui;

import ca.gc.dfo.psffs.controllers.*;
import ca.gc.dfo.psffs.controllers.lookups.*;
import ca.gc.dfo.psffs.services.LookupService;
import ca.gc.dfo.spring_commons.commons_offline_wet.ui.AbstractSiteMapContainer;
import ca.gc.dfo.spring_commons.commons_offline_wet.ui.SiteMapNode;
import org.springframework.stereotype.Component;

import static ca.gc.dfo.psffs.web.security.SecurityHelper.*;

@Component
public class SiteMapContainer extends AbstractSiteMapContainer
{
    @Override
    public void populateNodes()
    {
        //MENU NODES
        //Home >
        SiteMapNode cellDefinitions = addNode(new SiteMapNode("page.cell_definitions", "page.cell_definitions.desc", TemplateController.CELL_DEFINITIONS_PATH, false, true));
        SiteMapNode samplings = addNode(new SiteMapNode("page.samplings", "page.samplings.desc", TemplateController.SAMPLINGS_PATH, false, true));
        SiteMapNode settings = addNode(new SiteMapNode("page.settings", "page.settings.desc", TemplateController.SETTINGS_PATH, false, true));
        SiteMapNode serverTools = addNode(new SiteMapNode("page.server_tools", "page.server_tools.desc", TemplateController.SERVER_TOOLS_PATH, false, true));

        //Home > Cell Definitions >
        addNode(new SiteMapNode("page.cell_definitions.create", "page.cell_definitions.create.desc", CellDefinitionsController.CELL_DEFINITIONS_CREATE_PATH, EL_MODIFY_CELL_DEFINITIONS, false, true, true), cellDefinitions);
        SiteMapNode cdList = addNode(new SiteMapNode("page.cell_definitions.list", "page.cell_definitions.list.desc", CellDefinitionsController.CELL_DEFINITIONS_LIST_PATH, EL_VIEW_CELL_DEFINITIONS, false, true, true), cellDefinitions);

        //Home > Cell Definitions > List >
        addNode(new SiteMapNode("page.cell_definitions.edit", "page.cell_definitions.edit.desc", CellDefinitionsController.CELL_DEFINITIONS_EDIT_PATH, EL_VIEW_CELL_DEFINITIONS, true, false, true), cdList);

        //Home > Samplings >
        SiteMapNode sSamplingData = addNode(new SiteMapNode("page.samplings.sample_data", "page.samplings.sample_data.desc", SamplingDataController.SAMPLING_DATA_LIST_PATH, EL_VIEW_SAMPLING_DATA, false, true, true), samplings);
        SiteMapNode sLengthFrequency = addNode(new SiteMapNode("page.samplings.length_frequency", "page.samplings.length_frequency.desc", TemplateController.LENGTH_FREQUENCY_PATH, false, true), samplings);
        SiteMapNode sObserver = addNode(new SiteMapNode("page.samplings.observer", "page.samplings.observer.desc", TemplateController.OBSERVER_PATH, false, true), samplings);

        //Home > Samplings > Sampling data
        addNode(new SiteMapNode("page.samplings.sample_data.edit", "page.samplings.sample_data.edit.desc", SamplingDataController.SAMPLING_DATA_EDIT_PATH, EL_VIEW_SAMPLING_DATA, true, false, true), sSamplingData);

        //Home > Samplings > Length Frequency >
        addNode(new SiteMapNode("page.samplings.length_frequency.create", "page.samplings.length_frequency.create.desc", LengthFrequencyController.LENGTH_FREQUENCY_CREATE_PATH, EL_VIEW_LENGTH_FREQUENCIES, false, true, true), sLengthFrequency);
        SiteMapNode lfList = addNode(new SiteMapNode("page.samplings.length_frequency.list", "page.samplings.length_frequency.list.desc", LengthFrequencyController.LENGTH_FREQUENCY_LIST_PATH, EL_VIEW_LENGTH_FREQUENCIES, false, true, true), sLengthFrequency);
        addNode(new SiteMapNode("page.samplings.trip_settings.list", "page.samplings.trip_settings.list.desc", TripSettingsController.TRIP_SETTINGS_FORM_PATH, EL_VIEW_SAMPLING_SETTINGS, false, true, true), sLengthFrequency);

        //Home > Samplings > Length Frequency > List >
        addNode(new SiteMapNode("page.samplings.length_frequency.edit", "page.samplings.length_frequency.edit.desc", LengthFrequencyController.LENGTH_FREQUENCY_EDIT_PATH, EL_VIEW_LENGTH_FREQUENCIES, true, false, true), lfList);

        //Home > Samplings > Observer >
        addNode(new SiteMapNode("page.samplings.observer.create", "page.samplings.observer.create.desc", ObserverController.OBSERVER_CREATE_PATH, EL_MODIFY_TRIP_SET_SPECIES, false, true, true), sObserver);
        SiteMapNode tssList = addNode(new SiteMapNode("page.samplings.observer.list", "page.samplings.observer.list.desc", ObserverController.OBSERVER_LIST_PATH, EL_VIEW_TRIP_SET_SPECIES, false, true, true), sObserver);
        addNode(new SiteMapNode("page.samplings.observer_settings.list", "page.samplings.observer_settings.list.desc", ObserverSettingsController.OBSERVER_SETTINGS_FORM_PATH, EL_VIEW_SAMPLING_SETTINGS, false, true, true), sObserver);

        //Home > Samplings > Observer > List >
        addNode(new SiteMapNode("page.samplings.observer.edit", "page.samplings.observer.edit.desc", ObserverController.OBSERVER_EDIT_PATH, EL_VIEW_TRIP_SET_SPECIES, true, false, true), tssList);

        //Home > Settings >
        addNode(new SiteMapNode("page.settings.sampling_ranges", "page.settings.sampling_ranges.desc", SamplingRangesController.SAMPLING_RANGES_EDIT_PATH, false, true), settings);
        SiteMapNode tUserManagement = addNode(new SiteMapNode("page.settings.user_mgmt", "page.settings.user_mgmt.desc", TemplateController.USER_MANAGEMENT_PATH, false, true), settings);
        SiteMapNode tCodeTables = addNode(new SiteMapNode("page.settings.code_tables", "page.settings.code_tables.desc", TemplateController.CODE_TABLES_PATH, false, true), settings);
        SiteMapNode tSpecialTables = addNode(new SiteMapNode("page.settings.special_tables", "page.settings.special_tables.desc", TemplateController.SPECIAL_TABLES_PATH, false, true), settings);

        //Home > Settings > User Management >
        SiteMapNode userNode = addNode(new SiteMapNode("page.settings.user_mgmt.users", "page.settings.user_mgmt.users.desc", UserManagementController.USER_MANAGEMENT_LIST_PATH, EL_VIEW_USER_MANAGEMENT,false, true, true), tUserManagement);
        addNode(new SiteMapNode("page.settings.code_tables.any.create", null, UserManagementController.USER_MANAGEMENT_CREATE_PATH, EL_MODIFY_USER_MANAGEMENT, false, true, true), userNode);
        addNode(new SiteMapNode("page.settings.code_tables.any.edit", null, UserManagementController.USER_MANAGEMENT_EDIT_PATH, EL_VIEW_USER_MANAGEMENT, true, true, true), userNode);
        addNode(new SiteMapNode("page.settings.user_mgmt.roles", "page.settings.user_mgmt.roles.desc", UserManagementController.USER_MANAGEMENT_ROLE_PATH, false, true, true), tUserManagement);

        //Home > Settings > Code Tables >
        SiteMapNode listNode;
        for (LookupService.GenericLookup gl : LookupService.GenericLookup.values()) {
            listNode = addNode(new SiteMapNode("page.settings.code_tables."+gl.CODE_ATTR, "page.settings.code_tables."+gl.CODE_ATTR+".desc", GenericLookupController.GENERIC_LOOKUP_LIST_PATH.replaceAll("\\{codeAttr}", gl.CODE_ATTR), EL_VIEW_CODE_TABLES, false, true, true), tCodeTables);
            addNode(new SiteMapNode("page.settings.code_tables.any.create", null, GenericLookupController.GENERIC_LOOKUP_CREATE_PATH.replaceAll("\\{codeAttr}", gl.CODE_ATTR), EL_MODIFY_CODE_TABLES, false, false, true), listNode);
            addNode(new SiteMapNode("page.settings.code_tables.any.edit", null, GenericLookupController.GENERIC_LOOKUP_EDIT_PATH.replaceAll("\\{codeAttr}", gl.CODE_ATTR), EL_VIEW_CODE_TABLES, true, false, true), listNode);
        }

        //Home > Settings > Special Tables >
        //..Length categories >
        listNode = addNode(new SiteMapNode("page.settings.special_tables.length_categories", "page.settings.special_tables.length_categories.desc", LengthCategoryController.LENGTH_CAT_LIST_PATH, EL_VIEW_CODE_TABLES, false, true, true), tSpecialTables);
        addNode(new SiteMapNode("page.settings.code_tables.any.create", null, LengthCategoryController.LENGTH_CAT_CREATE_PATH, EL_MODIFY_CODE_TABLES, false, false, true), listNode);
        addNode(new SiteMapNode("page.settings.code_tables.any.edit", null, LengthCategoryController.LENGTH_CAT_EDIT_PATH, EL_VIEW_CODE_TABLES, true, false, true), listNode);
        //..Length groups >
        listNode = addNode(new SiteMapNode("page.settings.special_tables.length_groups", "page.settings.special_tables.length_groups.desc", LengthGroupController.LENGTH_GROUP_LIST_PATH, EL_VIEW_CODE_TABLES, false, true, true), tSpecialTables);
        addNode(new SiteMapNode("page.settings.code_tables.any.create", null, LengthGroupController.LENGTH_GROUP_CREATE_PATH, EL_MODIFY_CODE_TABLES, false, false, true), listNode);
        addNode(new SiteMapNode("page.settings.code_tables.any.edit", null, LengthGroupController.LENGTH_GROUP_EDIT_PATH, EL_VIEW_CODE_TABLES, true, false, true), listNode);
        //..Maturities >
        listNode = addNode(new SiteMapNode("page.settings.special_tables.maturities", "page.settings.special_tables.maturities.desc", MaturityController.MATURITY_LIST_PATH, EL_VIEW_CODE_TABLES, false, true, true), tSpecialTables);
        addNode(new SiteMapNode("page.settings.code_tables.any.create", null, MaturityController.MATURITY_CREATE_PATH, EL_MODIFY_CODE_TABLES, false, false, true), listNode);
        addNode(new SiteMapNode("page.settings.code_tables.any.edit", null, MaturityController.MATURITY_EDIT_PATH, EL_VIEW_CODE_TABLES, true, false, true), listNode);
        //..Quarters >
        listNode = addNode(new SiteMapNode("page.settings.special_tables.quarters", "page.settings.special_tables.quarters.desc", QuarterController.QUARTER_LIST_PATH, EL_VIEW_CODE_TABLES, false, true, true), tSpecialTables);
        addNode(new SiteMapNode("page.settings.code_tables.any.replace", null, QuarterController.QUARTER_REPLACE_PATH, EL_MODIFY_CODE_TABLES, false, false, true), listNode);
        //..Quotes >
        listNode = addNode(new SiteMapNode("page.settings.special_tables.countries", "page.settings.special_tables.countries.desc", QuotaController.QUOTA_LIST_PATH, EL_VIEW_CODE_TABLES, false, true, true), tSpecialTables);
        addNode(new SiteMapNode("page.settings.code_tables.any.create", null, QuotaController.QUOTA_CREATE_PATH, EL_MODIFY_CODE_TABLES, false, false, true), listNode);
        addNode(new SiteMapNode("page.settings.code_tables.any.edit", null, QuotaController.QUOTA_EDIT_PATH, EL_VIEW_CODE_TABLES, true, false, true), listNode);
        //..Species >
        listNode = addNode(new SiteMapNode("page.settings.special_tables.species", "page.settings.special_tables.species.desc", SpeciesController.SPECIES_LIST_PATH, EL_VIEW_CODE_TABLES, false, true, true), tSpecialTables);
        addNode(new SiteMapNode("page.settings.code_tables.any.create", null, SpeciesController.SPECIES_CREATE_PATH, EL_MODIFY_CODE_TABLES, false, false, true), listNode);
        addNode(new SiteMapNode("page.settings.code_tables.any.edit", null, SpeciesController.SPECIES_EDIT_PATH, EL_VIEW_CODE_TABLES, true, false, true), listNode);
        //..Unit areas >
        listNode = addNode(new SiteMapNode("page.settings.code_tables.unit_areas", "page.settings.code_tables.unit_areas.desc", UnitAreaController.UNIT_AREA_LIST_PATH, EL_VIEW_CODE_TABLES, false, true, true), tSpecialTables);
        addNode(new SiteMapNode("page.settings.code_tables.any.create", null, UnitAreaController.UNIT_AREA_CREATE_PATH, EL_MODIFY_CODE_TABLES, false, false, true), listNode);
        addNode(new SiteMapNode("page.settings.code_tables.any.edit", null, UnitAreaController.UNIT_AREA_EDIT_PATH, EL_VIEW_CODE_TABLES, true, false, true), listNode);
        //..Vessels >
        listNode = addNode(new SiteMapNode("page.settings.special_tables.vessels", "page.settings.special_tables.vessels.desc", VesselController.VESSEL_LIST_PATH, EL_VIEW_CODE_TABLES, false, true, true), tSpecialTables);
        addNode(new SiteMapNode("page.settings.code_tables.any.create", null, VesselController.VESSEL_CREATE_PATH, EL_MODIFY_CODE_TABLES, false, false, true), listNode);
        addNode(new SiteMapNode("page.settings.code_tables.any.edit", null, VesselController.VESSEL_EDIT_PATH, EL_VIEW_CODE_TABLES, true, false, true), listNode);
        //..Weight conversion factors >
        listNode = addNode(new SiteMapNode("page.settings.special_tables.weight_conversion_factors", "page.settings.special_tables.weight_conversion_factors.desc", WeightConversionFactorController.WCF_LIST_PATH, EL_VIEW_CODE_TABLES, false, true, true), tSpecialTables);
        addNode(new SiteMapNode("page.settings.code_tables.any.create", null, WeightConversionFactorController.WCF_CREATE_PATH, EL_MODIFY_CODE_TABLES, false, false, true), listNode);
        addNode(new SiteMapNode("page.settings.code_tables.any.edit", null, WeightConversionFactorController.WCF_EDIT_PATH, EL_VIEW_CODE_TABLES, true, false, true), listNode);

        //Home > Server Tools >
        addNode(new SiteMapNode("page.server_tools.extracts", "page.server_tools.extracts.desc", ServerToolsController.EXTRACTS_PATH, false, true), serverTools);
        addNode(new SiteMapNode("page.server_tools.reports", "page.server_tools.reports.desc", ServerToolsController.REPORTS_PATH, false, true), serverTools);
    }
}
