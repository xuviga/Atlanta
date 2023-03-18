package ru.imine.shared.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.imine.version.server.ServerMapper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

public class Location implements Cloneable, Serializable
{
    private static final long serialVersionUID = 322L;
    private static final WorldServer[] worldServers;

    private Integer dimensionId=null;
    private transient World world;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    static
    {
        if (FMLCommonHandler.instance().getSide()==Side.SERVER)
            worldServers = ServerMapper.instance().getWorlds();
        else
            worldServers = null;
    }

    public Location(World world, BlockPos pos)
    {
        this(world,pos.getX(),pos.getY(),pos.getZ());
    }

    public Location(World world, double x, double y, double z)
    {
        this(world, x, y, z, 0.0F, 0.0F);
    }

    public Location(World world, double x, double y, double z, float yaw, float pitch)
    {
        this.world = world;
        this.dimensionId = world.provider.getDimension();
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public Location(Entity entity)
    {
        this(entity.getEntityWorld(), entity.posX, entity.posY, entity.posZ, entity.rotationPitch, entity.rotationYaw);
    }

    @SideOnly(Side.SERVER)
    public void setWorld(World world)
    {
        this.world = world;
    }

    public Integer getDimensionId()
    {
        return dimensionId;
    }

    @SideOnly(Side.SERVER)
    public World getWorld()
    {
        return world;
    }

    public Chunk getChunk()
    {
        return this.world.getChunk(new BlockPos(x,y,z));
    }

    public BlockPos getBlockPos()
    {
        return new BlockPos(x,y,z);
    }

    public IBlockState getBlockState()
    {
        return this.world.getBlockState(new BlockPos(x,y,z));
    }

    public TileEntity getTileEntity()
    {
        return this.world.getTileEntity(new BlockPos(x,y,z));
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getX()
    {
        return this.x;
    }

    public int getBlockX()
    {
        return (int)Math.floor(x);
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getY()
    {
        return y;
    }

    public int getBlockY()
    {
        return (int)Math.floor(y);
    }

    public void setZ(double z)
    {
        this.z = z;
    }

    public double getZ()
    {
        return this.z;
    }

    public int getBlockZ()
    {
        return (int)Math.floor(z);
    }

    public void setYaw(float yaw)
    {
        this.yaw = yaw;
    }

    public float getYaw()
    {
        return this.yaw;
    }

    public void setPitch(float pitch)
    {
        this.pitch = pitch;
    }

    public float getPitch()
    {
        return this.pitch;
    }

    public Location add(Location vec)
    {
        if (vec != null && Objects.equals(vec.dimensionId, dimensionId))
        {
            this.x += vec.x;
            this.y += vec.y;
            this.z += vec.z;
            return this;
        }
        else
        {
            throw new IllegalArgumentException("Cannot add Locations of differing worlds");
        }
    }

    public Location add(double x, double y, double z)
    {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Location subtract(Location vec)
    {
        if (vec != null && Objects.equals(vec.dimensionId, dimensionId))
        {
            this.x -= vec.x;
            this.y -= vec.y;
            this.z -= vec.z;
            return this;
        }
        else
        {
            throw new IllegalArgumentException("Cannot add Locations of differing worlds");
        }
    }

    public Location subtract(double x, double y, double z)
    {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public double length()
    {
        return Math.sqrt(x*x + y*y + z*z);
    }

    public double lengthSquared()
    {
        return x * x + y * y + z * z;
    }

    public double distance(Location o)
    {
        return Math.sqrt(this.distanceSquared(o));
    }

    public double distanceSquared(Location o)
    {
        if (o == null || o.dimensionId == null || dimensionId == null)
        {
            return Double.POSITIVE_INFINITY;
        }
        if (!Objects.equals(o.dimensionId, this.dimensionId))
        {
            return Double.POSITIVE_INFINITY;
        }
        else
        {
            double dx = x - o.x;
            double dy = y - o.y;
            double dz = z - o.z;
            return dx * dx + dy * dy + dz * dz;
        }
    }

    public Location multiply(double m)
    {
        this.x *= m;
        this.y *= m;
        this.z *= m;
        return this;
    }

    public Location zero()
    {
        this.x = 0.0D;
        this.y = 0.0D;
        this.z = 0.0D;
        return this;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (this.getClass() != obj.getClass())
        {
            return false;
        }
        else
        {
            Location other = (Location) obj;
            if (this.world != other.world && (this.world == null || !this.world.equals(other.world)))
            {
                return false;
            }
            else if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x))
            {
                return false;
            }
            else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y))
            {
                return false;
            }
            else if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z))
            {
                return false;
            }
            else if (Float.floatToIntBits(this.pitch) != Float.floatToIntBits(other.pitch))
            {
                return false;
            }
            else
            {
                return Float.floatToIntBits(this.yaw) == Float.floatToIntBits(other.yaw);
            }
        }
    }

    public int hashCode()
    {
        int hash = 3;
        hash = 19 * hash + (this.world != null ? this.world.hashCode() : 0);
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.x) ^ Double.doubleToLongBits(this.x) >>> 32);
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.y) ^ Double.doubleToLongBits(this.y) >>> 32);
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.z) ^ Double.doubleToLongBits(this.z) >>> 32);
        hash = 19 * hash + Float.floatToIntBits(this.pitch);
        hash = 19 * hash + Float.floatToIntBits(this.yaw);
        return hash;
    }

    public String toString()
    {
        return "Location{world=" + this.dimensionId + ",x=" + this.x + ",y=" + this.y + ",z=" + this.z + ",pitch=" + this.pitch + ",yaw=" + this.yaw + '}';
    }

    public Location clone()
    {
        try
        {
            return (Location) super.clone();
        }
        catch (CloneNotSupportedException var2)
        {
            throw new Error(var2);
        }
    }

    private void writeObject(ObjectOutputStream oos) throws IOException
    {
        oos.writeInt(dimensionId);
        oos.writeDouble(x);
        oos.writeDouble(y);
        oos.writeDouble(z);
        oos.writeFloat(pitch);
        oos.writeFloat(yaw);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
    {
        dimensionId=ois.readInt();
        x = ois.readDouble();
        y = ois.readDouble();
        z = ois.readDouble();
        pitch = ois.readFloat();
        yaw = ois.readFloat();
        if (FMLCommonHandler.instance().getSide()==Side.SERVER)
            for (WorldServer worldServer : worldServers)
                if (worldServer.provider.getDimension()==dimensionId)
                    world = worldServer;
    }
}