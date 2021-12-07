package api;

import api.GeoLocation;

public class GeoLocationImp implements GeoLocation
{
    private double _x, _y, _z;
    private boolean _isValid;

    public GeoLocationImp(GeoLocation other)
    {
        this(other.x(), other.y(), other.z());
    }

    public GeoLocationImp()
    {
        this(0,0,0);
    }
    public GeoLocationImp(double x, double y)
    {
        this(x,y,0);
    }
    public GeoLocationImp(double x, double y, double z)
    {
        _x = x;
        _y = y;
        _z = z;
        _isValid = true;
    }

    public GeoLocationImp(String pos)
    {
        this();

        String[] geoPlace = pos.split(",");
        try
        {
            _x = Double.parseDouble(geoPlace[0]);
            _y = Double.parseDouble(geoPlace[1]);
            _z = Double.parseDouble(geoPlace[2]);
        }
        catch (IndexOutOfBoundsException ex)
        {
            _isValid = true;
        }
        catch (Exception ex)
        {
            _isValid = false;
        }
    }

    @Override
    public double x()
    {
        return _x;
    }

    @Override
    public double y()
    {
        return _y;
    }

    @Override
    public double z()
    {
        return _z;
    }

    @Override
    public double distance(GeoLocation g)
    {
        double distX = Math.pow(g.x() - this.x(), 2.0);
        double distY = Math.pow(g.y() - this.y(), 2.0);
        double distZ = Math.pow(g.z() - this.z(), 2.0);

        return Math.sqrt(distX + distY + distZ);
    }

    @Override
    public String toString() {
        return _x + "," + _y + "," + _z;
    }
}
