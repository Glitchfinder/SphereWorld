package net.zhuoweizhang.moresphereworld.chunk;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class SphereChunkGenerator extends ChunkGenerator {

    public byte[][] generateBlockSections(World world, Random random, int i, int j, ChunkGenerator.BiomeGrid biomes) {
        throw new UnsupportedOperationException("Herp derp!");
	/*byte[] abyte = new byte['\u8000'];
	Chunk chunk = new Chunk(this.p, abyte, i, j);
	this.v = this.p.getWorldChunkManager().a(this.v, i * 16, j * 16, 16, 16);
	double[] adouble = this.p.getWorldChunkManager().rain;
	this.a(i, j, abyte, this.v, adouble);
	this.a(i, j, abyte, this.v);
	this.u.a(this, this.p, i, j, abyte);
	BlockSand.instaFall = true;
	boolean hassphere = false;
	Spheres ts = new Spheres();
	ts.GetSphereList().clear();
	for (Sphere s : ss.GetSphereList()) {
	    if (s.getX() > i * 16 - SphereWorldConfig.maxradius && s.getX() < i * 16 + SphereWorldConfig.maxradius + 16) {
		if (s.getZ() > j * 16 - SphereWorldConfig.maxradius && s.getZ() < j * 16 + SphereWorldConfig.maxradius + 16) {
		    ts.AddSphereToList(s);
		    hassphere = true;
		}
	    }
	}
	if (hassphere) {
	    if (SphereWorldConfig.userandomglass && ts.GetSphereList().size() > 1) {
		// Lets sort this to get the right random	
		vt = new Vector((double) i * 16 + 8 , (double) 64, (double) j * 16 + 8);
		Collections.sort(ts.GetSphereList(), COMPARATOR);
	    }
	    if (SphereWorldConfig.useglass || (SphereWorldConfig.userandomglass && (ts.GetSphereList().get(0).getSize() % 2 == 1))) {
		    for (int k = 0; k < 16; ++k) {
			for (int l = 0; l < 16; ++l) {
			    for (int k1 = 127; k1 >= 2; --k1) {
				int l1 = (l * 16 + k) * 128 + k1;
				 boolean keep = false;
				 for (Sphere s : ts.GetSphereList()) {
				     if (s.getV().distance(new Vector((double) i * 16 + l, (double) k1, (double) j * 16 + k)) < s.getSize()) {
					 keep = true;
					 if (s.getV().distance(new Vector((double) i * 16 + l, (double) k1, (double) j * 16 + k)) > s.getSize() - 1.1) {
					    if (SphereWorldConfig.useglow) {
						if (s.getX() == i* 16 + l || s.getZ() ==  j * 16 + k) {
						    abyte[l1] = (byte) 89;
						} else {
						    abyte[l1] = (byte) SphereWorldConfig.glassblock;
					    	}
					    } else {
						abyte[l1] = (byte) SphereWorldConfig.glassblock;
					    }					     
					 }
				     }
				 }
				 if (!keep) abyte[l1] = (byte) 0x0;
			    }
			   
			}
		    }
	    } else if (SphereWorldConfig.usehalfglass || (SphereWorldConfig.userandomglass && (ts.GetSphereList().get(0).getSize() % 2 == 0))) {
		 for (int k = 0; k < 16; ++k) {
			for (int l = 0; l < 16; ++l) {
			    for (int k1 = 127; k1 >= 64; --k1) {
				int l1 = (l * 16 + k) * 128 + k1;
				 boolean keep = false;
				 for (Sphere s : ts.GetSphereList()) {
				     if (s.getV().distance(new Vector((double) i * 16 + l, (double) k1, (double) j * 16 + k)) < s.getSize()) {
					 keep = true;
				     }
				 }
				 if (!keep) abyte[l1] = (byte) 0x0;
			    }
			   
			}
		    }
		 for (int k = 0; k < 16; ++k) {
			for (int l = 0; l < 16; ++l) {
			    for (int k1 = 64; k1 >= 2; --k1) {
				int l1 = (l * 16 + k) * 128 + k1;
				 boolean keep = false;
				 for (Sphere s : ts.GetSphereList()) {
				     if (s.getV().distance(new Vector((double) i * 16 + l, (double) k1, (double) j * 16 + k)) < s.getSize()) {
					 keep = true;
					 if (s.getV().distance(new Vector((double) i * 16 + l, (double) k1, (double) j * 16 + k)) > s.getSize() - 1.1) {
					     if (SphereWorldConfig.useglow) {
						if (s.getX() == i* 16 + l || s.getZ() ==  j * 16 + k) {
						    abyte[l1] = (byte) 89;
						} else {
						    abyte[l1] = (byte) SphereWorldConfig.glassblock;
					    	}
					    } else {
						abyte[l1] = (byte) SphereWorldConfig.glassblock;
					    }		
					 }
				     }
				 }
				 if (!keep) abyte[l1] = (byte) 0x0;
			    }
			   
			}
		   }
		
	    }
	    else {
		for (int k = 0; k < 16; ++k) {
			for (int l = 0; l < 16; ++l) {
			    for (int k1 = 127; k1 >= 2; --k1) {
				int l1 = (l * 16 + k) * 128 + k1;
				 boolean keep = false;
				 for (Sphere s : ts.GetSphereList()) {
				     if (s.getV().distance(new Vector((double) i * 16 + l, (double) k1, (double) j * 16 + k)) < s.getSize()) {
					 keep = true;
				     }
				 }
				 if (!keep) abyte[l1] = (byte) 0x0;
			    }
			   
			}
		}
	    }	  
	} else {
	    for (int k = 0; k < 16; ++k) {
		for (int l = 0; l < 16; ++l) {
		    for (int k1 = 127; k1 >= 2; --k1) {
			int l1 = (l * 16 + k) * 128 + k1;
			abyte[l1] = (byte) 0x0;
		    }
		}
	    } 
	}
	if (SphereWorldConfig.usefloor) {
	 for (int k = 0; k < 16; ++k) {
		for (int l = 0; l < 16; ++l) {
		   int k1 = 1;
		   	int l1 = (l * 16 + k) * 128 + k1;
		   	if (!SphereWorldConfig.nowater) {
		   	    abyte[l1] = (byte) 9; 
		   	} else {
		   	    abyte[l1] = (byte) 0; 
		   	} 
			k1 = 0;
			l1 = (l * 16 + k) * 128 + k1;
			abyte[l1] = (byte) 7;
		}
	    } 
	} else{
	    for (int k = 0; k < 16; ++k) {
		for (int l = 0; l < 16; ++l) {
		   int k1 = 1;
		   	int l1 = (l * 16 + k) * 128 + k1;
			abyte[l1] = (byte) 0x0;
			k1 = 0;
			l1 = (l * 16 + k) * 128 + k1;
			abyte[l1] = (byte) 0x0;
		}
	    } 
	}
	  
	BlockSand.instaFall = false;
	chunk.initLighting();
	plugin.oldchunks.AddChunkToList(SphereWorldConfig.world,i,j);
	return chunk;*/
    }
}
