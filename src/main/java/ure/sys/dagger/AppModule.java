package ure.sys.dagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.eventbus.EventBus;
import dagger.Module;
import dagger.Provides;
import testRogue.commander.TestUCommander;
import testRogue.map.TestCatrographer;
import ure.areas.gen.LandscaperDeserializer;
import ure.areas.UCartographer;
import ure.areas.gen.ULandscaper;
import ure.actors.behaviors.BehaviorDeserializer;
import ure.actors.behaviors.UBehavior;
import ure.math.URandom;
import ure.render.URenderer;
import ure.render.URendererOGL;
import ure.sys.ResourceManager;
import ure.sys.UCommander;
import ure.actors.ActorDeserializer;
import ure.actors.UActor;
import ure.actors.UActorCzar;
import ure.sys.UConfig;
import ure.terrain.TerrainDeserializer;
import ure.terrain.UTerrain;
import ure.terrain.UTerrainCzar;
import ure.things.ThingDeserializer;
import ure.things.UThing;
import ure.things.UThingCzar;
import ure.ui.Icons.Icon;
import ure.ui.Icons.IconDeserializer;
import ure.ui.Icons.UIconCzar;
import ure.ui.sounds.USpeaker;

import javax.inject.Singleton;

/**
 *
 * The register of providers for dependency injection.
 *
 */
@Module
public class AppModule {

    @Provides
    @Singleton
    public ObjectMapper providesObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(UTerrain.class, new TerrainDeserializer(objectMapper));
        module.addDeserializer(UThing.class, new ThingDeserializer(objectMapper));
        module.addDeserializer(UActor.class, new ActorDeserializer(objectMapper));
        module.addDeserializer(ULandscaper.class, new LandscaperDeserializer(objectMapper));
        module.addDeserializer(UBehavior.class, new BehaviorDeserializer(objectMapper));
        module.addDeserializer(Icon.class, new IconDeserializer(objectMapper));
        objectMapper.registerModule(module);
        return objectMapper;
    }

    @Provides
    @Singleton
    public UCommander providesCommander() {
        UCommander cmdr = new TestUCommander();
        return cmdr;
    }

    @Provides
    @Singleton
    public URenderer providesRenderer() {
        URenderer rend = new URendererOGL();
        rend.initialize();
        return rend;
    }

    @Provides
    @Singleton
    public UActorCzar providesActorCzar() {
        UActorCzar czar = new UActorCzar();
        //czar.loadActors("/actors.json"); -- can't do this here because it relies on some other things being loaded
        return czar;
    }

    @Provides
    @Singleton
    public UTerrainCzar providesTerrainCzar() {
        UTerrainCzar czar = new UTerrainCzar();
        czar.loadTerrains();
        return czar;
    }

    @Provides
    @Singleton
    public UThingCzar providesThingCzar() {
        UThingCzar czar = new UThingCzar();
        czar.loadThings();
        return czar;
    }

    @Provides
    @Singleton
    public UIconCzar providesIconCzar() {
        UIconCzar czar = new UIconCzar();
        czar.loadIcons();
        return czar;
    }

    @Provides
    @Singleton
    public UCartographer providesCartographer() {
        UCartographer cartographer = new TestCatrographer();
        return cartographer;
    }

    @Provides
    @Singleton
    public EventBus providesEventBus() {
        return new EventBus();
    }

    @Provides
    @Singleton
    public UConfig providesConfig() {
        return new UConfig();
    }

    @Provides
    @Singleton
    public URandom providesRandom() {
        return new URandom();
    }

    @Provides
    @Singleton
    public USpeaker providesSpeaker() {
        USpeaker s = new USpeaker();
        s.initialize();
        return s;
    }

    @Provides
    @Singleton
    public ResourceManager providesResourceManager() {
        return new ResourceManager();
    }
}
