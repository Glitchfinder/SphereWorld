package net.zhuoweizhang.moresphereworld.chunk;

import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import org.bukkit.block.Block;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.Vector;

import net.zhuoweizhang.moresphereworld.Sphere;
import net.zhuoweizhang.moresphereworld.Spheres;

import net.zhuoweizhang.moresphereworld.config.SphereWorldConfig;

public class SphereBlockPopulator extends BlockPopulator {

    public Spheres spheres;

    private Vector vt = null; //for some reason, this is a class scope variable in the original. Why?

    private Comparator<Sphere> COMPARATOR = new Comparator<Sphere>() 
        {
        // This is where the sorting happens.
            public int compare(Sphere o1, Sphere o2)
            {
                return (int) ((o1.getV().distance(vt) - o1.getSize()) - (o2.getV().distance(vt) - o2.getSize()));
            }
        };

    /**
     * Populates a Chunk with spheres by changing all non-sphere areas to air and wrapping the resulting land in glass.
     */

    public void populate(World world, Random random, Chunk chunk) {
        int worldHeight = 128; // not world.getMaxHeight() as terrain gen will never go above 128;
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        boolean hassphere = false;
	Spheres ts = new Spheres(); //spheres in the current chunk
	ts.getSphereList().clear();
	for (Sphere s : spheres.getSphereList()) {
	    if (s.getX() > chunkX * 16 - SphereWorldConfig.maxradius && s.getX() < chunkX * 16 + SphereWorldConfig.maxradius + 16) {
		if (s.getZ() > chunkZ * 16 - SphereWorldConfig.maxradius && s.getZ() < chunkZ * 16 + SphereWorldConfig.maxradius + 16) {
		    ts.addSphereToList(s);
		    hassphere = true;
		}
	    }
	}
	if (hassphere) {
	    if (SphereWorldConfig.userandomglass && ts.getSphereList().size() > 1) {
		// Let's sort this to get the right random	
		vt = new Vector((double) chunkX * 16 + 8 , (double) 64, (double) chunkZ * 16 + 8);
		Collections.sort(ts.getSphereList(), COMPARATOR);
	    }
	    if (SphereWorldConfig.useglass || (SphereWorldConfig.userandomglass && (ts.getSphereList().get(0).getSize() % 2 == 1))) {
		    for (int z = 0; z < 16; ++z) {
			for (int x = 0; x < 16; ++x) {
			    for (int y = worldHeight - 1; y >= 2; --y) {
                                 Block block = chunk.getBlock(x, y, z);
				 boolean keep = false;
				 for (Sphere s : ts.getSphereList()) {
				     if (s.getV().distance(new Vector((double) chunkX * 16 + x, (double) y, (double) chunkZ * 16 + z)) < s.getSize()) {
					 keep = true;
					 if (s.getV().distance(new Vector((double) chunkX * 16 + x, (double) y, (double) chunkZ * 16 + z)) > s.getSize() - 1.1) {
					    if (SphereWorldConfig.useglow) {
						if (s.getX() == chunkX * 16 + x || s.getZ() ==  chunkZ * 16 + z) {
						    block.setTypeIdAndData(89, (byte) 0, false);
						} else {
						    block.setTypeIdAndData(SphereWorldConfig.glassblock, (byte) 0, false);
					    	}
					    } else {
						block.setTypeIdAndData(SphereWorldConfig.glassblock, (byte) 0, false);
					    }					     
					 }
				     }
				 }
				 if (!keep) block.setTypeIdAndData(0, (byte) 0, false);
			    }
			   
			}
		    }
	    } else if (SphereWorldConfig.usehalfglass || (SphereWorldConfig.userandomglass && (ts.getSphereList().get(0).getSize() % 2 == 0))) {
                    for (int z = 0; z < 16; ++z) {
			for (int x = 0; x < 16; ++x) {
			    for (int y = worldHeight - 1; y >= 64; --y) {
				 Block block = chunk.getBlock(x, y, z);
				 boolean keep = false;
				 for (Sphere s : ts.getSphereList()) {
				     if (s.getV().distance(new Vector((double) chunkX * 16 + x, (double) y, (double) chunkZ * 16 + z)) < s.getSize()) {
					 keep = true;
				     }
				 }
				 if (!keep) block.setTypeIdAndData(0, (byte) 0, false);
			    }
			   
			}
		    }
		    for (int z = 0; z < 16; ++z) {
			for (int x = 0; x < 16; ++x) {
			    for (int y = 64; y >= 2; --y) {
				 Block block = chunk.getBlock(x, y, z);
				 boolean keep = false;
                                 for (Sphere s : ts.getSphereList()) {
				     if (s.getV().distance(new Vector((double) chunkX * 16 + x, (double) y, (double) chunkZ * 16 + z)) < s.getSize()) {
					 keep = true;
					 if (s.getV().distance(new Vector((double) chunkX * 16 + x, (double) y, (double) chunkZ * 16 + z)) > s.getSize() - 1.1) {
					    if (SphereWorldConfig.useglow) {
						if (s.getX() == chunkX * 16 + x || s.getZ() ==  chunkZ * 16 + z) {
						    block.setTypeIdAndData(89, (byte) 0, false);
						} else {
						    block.setTypeIdAndData(SphereWorldConfig.glassblock, (byte) 0, false);
					    	}
					    } else {
						block.setTypeIdAndData(SphereWorldConfig.glassblock, (byte) 0, false);
					    }					     
					 }
				     }
				 }
				 if (!keep) block.setTypeIdAndData(0, (byte) 0, false);
			    }
			   
			}
		   }
		
	    }
	    else {
		for (int z = 0; z < 16; ++z) {
			for (int x = 0; x < 16; ++x) {
			    for (int y = worldHeight - 1; y >= 2; --y) {
				 Block block = chunk.getBlock(x, y, z);
				 boolean keep = false;
				 for (Sphere s : ts.getSphereList()) {
				     if (s.getV().distance(new Vector((double) chunkX * 16 + x, (double) y, (double) chunkZ * 16 + z)) < s.getSize()) {
					 keep = true;
				     }
				 }
				 if (!keep) block.setTypeIdAndData(0, (byte) 0, false);
			    }
			   
			}
		}
	    }	  
	} else {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    for (int y = worldHeight - 1; y >= 2; --y) {
                        chunk.getBlock(x, y, z).setTypeIdAndData(0, (byte) 0, false);
                    }
                }
            }
	}
	if (SphereWorldConfig.usefloor) {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    chunk.getBlock(x, 1, z).setTypeIdAndData((SphereWorldConfig.nowater? 0 : 9), (byte) 0, false);
                    chunk.getBlock(x, 0, z).setTypeIdAndData(7, (byte) 0, false);
		}
	    } 
	} else{
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    chunk.getBlock(x, 1, z).setTypeIdAndData(0, (byte) 0, false);
                    chunk.getBlock(x, 0, z).setTypeIdAndData(0, (byte) 0, false);
		}
	    }
	}
    }
}
