package laba_1.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import laba_1.model.Hero;
import laba_1.model.Occupant;
import laba_1.model.units.*;
import laba_1.model.buildings.*;

public class JsonUtil {
    public static Gson createGameGson() {
        var buildingAdapter = RuntimeTypeAdapterFactory
                .of(Building.class, "type")
                .registerSubtype(Tavern.class)
                .registerSubtype(Watchtower.class)
                .registerSubtype(CrossbowTower.class)
                .registerSubtype(Armory.class)
                .registerSubtype(Arena.class)
                .registerSubtype(Cathedral.class);

        var unitAdapter = RuntimeTypeAdapterFactory
                .of(Unit.class, "type")
                .registerSubtype(Spearman.class)
                .registerSubtype(Swordsman.class)
                .registerSubtype(Crossbowman.class)
                .registerSubtype(Cavalry.class)
                .registerSubtype(Paladin.class);

        var occupantAdapter = RuntimeTypeAdapterFactory
                .of(Occupant.class, "type")
                .registerSubtype(Castle.class)
                .registerSubtype(Hero.class);

        return new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapterFactory(buildingAdapter)
                .registerTypeAdapterFactory(unitAdapter)
                .registerTypeAdapterFactory(occupantAdapter)
                .create();
    }
}