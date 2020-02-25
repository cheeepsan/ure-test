package ure.editors.vaulted;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.actors.actions.UAction;
import ure.areas.UArea;
import ure.areas.UCell;
import ure.areas.gen.UVault;
import ure.math.UColor;
import ure.terrain.UTerrain;

public class VaultedArea extends UArea {

    private Log log = LogFactory.getLog(VaultedArea.class);

    public VaultedArea(int thexsize, int theysize) {
        super(thexsize, theysize, "null");
        label = "vaulted";
        resetSunColorLerps();
        addSunColorLerp(0, UColor.WHITE);
        addSunColorLerp(24*60-1, UColor.WHITE);
        setSunColor(1f,1f,1f);
        sunVisible = false;
    }

    @Override
    public void wakeCheckAll(int playerx, int playery) {

    }

    @Override
    public void broadcastEvent(UAction action) {

    }

    public void resize(int newx, int newy) {
        UCell[][] newcells = new UCell[newx][newy];
        for (int x=0;x<newx;x++) {
            for (int y=0;y<newy;y++) {
                newcells[x][y] = cells[x][y];
            }
        }
        xsize = newx;
        ysize = newy;
    }

    public void loadVault(UVault vault) {
        log.info("Loading vault...");
            resize(vault.getCols(), vault.getRows());
        for (int x=0;x<vault.getCols();x++) {
            for (int y=0;y<vault.getRows();y++) {
                setTerrain(x,y,vault.terrainAt(x,y));
            }
        }
        commander.player().moveToCell(this, 1,1);
    }


    public void initialize(int newx, int newy) {
        xsize = newx;
        ysize = newy;
        for (int x=0;x<xsize;x++) {
            for (int y=0;y<ysize;y++) {
                cells[x][y] = new UCell(this,x,y,terrainCzar.getTerrainForFilechar('@'));
            }
        }
    }

    public void saveVault(UVault vault) {
        log.info("Saving vault " + vault.getName() + " to vaultset...");
        String[][] tlines = new String[xsize][ysize];
        for (int x=0; x<vault.getCols();x++) {
            for (int y=0;y<vault.getRows();y++) {
                UTerrain t = terrainAt(x,y);
                if (t == null) tlines[x][y] = "null";
                else tlines[x][y] = t.getName();
            }
        }
        vault.setTerrain(tlines);
    }

    @Override
    public boolean canBePersisted() {
        return false;
    }
}
