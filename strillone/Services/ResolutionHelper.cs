using Microsoft.Phone.Info;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace strillone
{
 public enum Resolutions { WVGA, WXGA, HD720p, HD1080p };

public static class ResolutionHelper
{
    static private Size _size; 

    private static bool IsWvga
    {
        get
        {
            return App.Current.Host.Content.ScaleFactor == 100;
        }
    }

    private static bool IsWxga
    {
        get
        {
            return App.Current.Host.Content.ScaleFactor == 160;
        }
    }

    private static bool Is720p
    {
        get
        {
            return (App.Current.Host.Content.ScaleFactor == 150 && !Is1080p);
        }
    }

    private static bool Is1080p
    {
        get
        {
            if(_size.Width == 0)
            {
                try
                {
                    _size = (Size)DeviceExtendedProperties.GetValue("PhysicalScreenResolution");
                }
                catch (Exception)
                {
                    _size.Width = 0;
                }
            }
            return _size.Width == 1080;
        }
    }

    public static Resolutions CurrentResolution
    {
        get
        {
            if (IsWvga) return Resolutions.WVGA;
            else if (IsWxga) return Resolutions.WXGA;
            else if (Is720p) return Resolutions.HD720p;
            else if (Is1080p) return Resolutions.HD1080p;
            else throw new InvalidOperationException("Unknown resolution");
        }
    }
  }
}