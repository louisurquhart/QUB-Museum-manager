package part01;

public enum MenuStates {
    // Menu Level 1 states:
    MAIN_MENU,

    // Menu Level 2 states:
    MANAGE_ARTIFACTS,
    MANAGE_EXHIBITS,
    MANAGE_ANNUAL_PLANS,

    // Menu Level 3 states:

    // MANAGE ARTIFACTS - Artifact management states
    ADD_ARTIFACT,
    VIEW_ARTIFACT,
    DELETE_ARTIFACT,
    UPDATE_ARTIFACT,

    // MANAGE_EXHIBITS - Exhibit management states
    ADD_EXHIBIT,
    VIEW_EXHIBIT,
    DELETE_EXHIBIT,
    UPDATE_EXHIBIT,
    ADD_ARTIFACT_TO_EXHIBIT,
    DELETE_ARTIFACT_IN_EXHIBIT,
    REORDER_ARTIFACTS_IN_EXHIBIT,


    // MANAGE-ANNUAL-PLANS - Annual plan management states
    ADD_ANNUAL_PLAN,
    VIEW_ANNUAL_PLAN,
    DELETE_ANNUAL_PLAN,
    UPDATE_ANNUAL_PLAN,
    ADD_EXHIBIT_TO_ANNUAL_PLAN,
    DELETE_EXHIBIT_IN_ANNUAL_PLAN,


    // Menu level 4 states:

    // ADD_ARTIFACT -> Enters in artifact details

    // VIEW_ARTIFACT -> Requests artifacts ID, name, part-name or type

    // DELETE_ARTIFACT -> Requests artifacts ID

    // UPDATE_ARTIFACTS -> Requests artifacts ID

    // FIND ARTIFACTS -> Requests artifacts ID, name, part-name or type



}
