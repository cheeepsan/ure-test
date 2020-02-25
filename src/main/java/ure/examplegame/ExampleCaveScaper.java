package ure.examplegame;

import ure.areas.gen.Shape;
import ure.areas.UArea;
import ure.math.UColor;
import ure.areas.gen.ULandscaper;

public class ExampleCaveScaper extends ULandscaper {

    public static final String TYPE = "cavescaper";

    public ExampleCaveScaper() {
        super(TYPE);
    }

    @Override
    public void buildArea(UArea area, int level, String[] tags) {
        buildCaves(area, "floor", 0,0,area.xsize-1, area.ysize-1);
        scatterActorsByTags(area,1,1,area.xsize-1,area.ysize-1, new String[]{"cave"}, level, 10);
    }

    public void buildCaves(UArea area, String floorTerrain,
                           int x1, int y1, int x2, int y2) {

        float lavaFactor = randf(0.2f);
        if (randf() < 0.1f)
            lavaFactor = 1f;
        fillRect(area,"rock",x1,y1,x2,y2);
        //digCaves(area, floorTerrain, x1, x2, y1, y2,0.38f + randf(0.14f),4 + rand(3), 3 + rand(3),2 + rand(3));
        Shape caves = shapeCaves(x2-x1,y2-y1);
        caves.writeTerrain(area, "floor", x1, y1);

        if (randf() < 0.7f) {
            int rivers = rand(3) + 1;
            if (randf() < 0.1) rivers = rivers + rand(4);
            String water = "water";
            if (randf() < lavaFactor) water = "lava";
            Shape river = shapeRoad(x2-x1,y2-y1,2f+randf(4f), 0.4f+randf(2f), 0.6f+randf(2.5f));
            river.writeTerrain(area,water,x1,y1);
        }

        addDoors(area, "door", new String[]{"rock"}, 0.1f + randf(1.5f));

        int statues = rand(5);
        for (int i = 0;i < statues;i++) {
            int width = rand(5) + 2;
            int height = rand(5) + 2;
            int[] boxloc = locateBox(area, width, height, new String[]{floorTerrain});
            if (boxloc != null) {
                drawRect(area, "carvings", boxloc[0], boxloc[1], boxloc[0] + width, boxloc[1] + height);
                if (randf() < 0.7f) {
                    spawnThingAt(area, boxloc[0] + (width / 2), boxloc[1] + (height / 2), "gold statue");
                    spawnLightAt(area, boxloc[0] + (width / 2), boxloc[1] + (height / 2),
                            new UColor(1f, 1f, 0.7f), (width + height) / 2, 15);
                }
            }
        }

        String lights = "crystal stalagmite";
        if (randf() < lavaFactor) {
            lights = "magma vent";
            if (randf() < 0.3f) {
                simplexScatterThings(area, "crystal stalagmite", new String[]{"floor"}, randf(0.4f) + 0.3f, randf(0.07f),
                        new float[]{8f, 21f, randf(120f)}, 3);
            }
        }
        if (randf() < 0.6f) {
            simplexScatterThings(area, lights, new String[]{"floor"}, randf(0.4f) + 0.3f, randf(0.07f),
                    new float[]{9f, 23f, randf(100f) + 60f}, 3);
        }

        simplexScatterThings(area, "skull", new String[]{floorTerrain},
                0.65f, 0.1f + randf(0.2f),
                new float[]{5+randf(2f), 10f, 40f, 120f+randf(50f)}, 0);

        simplexScatterTerrain(area, "floormoss", new String[]{floorTerrain},
                randf(0.3f), randf(0.5f),
                new float[]{5f, 10f, 40f, 120f});

        scatterThings(area, new String[]{"trucker hat", "butcher knife", "apple", "small stone", "small stone"},
                new String[]{floorTerrain}, 10 + rand(40));
    }
}
