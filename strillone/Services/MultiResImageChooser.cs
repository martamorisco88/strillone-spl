using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using System.Windows.Media.Imaging;

namespace strillone
{
    public class MultiResImageChooser
    {
        public Uri BestResolutionImage
        {
            get
            {
                switch (ResolutionHelper.CurrentResolution)
                {
                    case Resolutions.HD1080p:
                        return new Uri("../Image/1080p/marchio.png", UriKind.Relative);
                    case Resolutions.HD720p:
                        //return 16:9 aspect ratio asset, high res
                        return new Uri("../Image/720p/marchio.png", UriKind.Relative);
                    case Resolutions.WXGA:
                        return new Uri("../Image/720p/marchio.png", UriKind.Relative);
                    case Resolutions.WVGA:
                        // return 15:9 aspect ratio asset, high res
                        return new Uri("../Image/WVGA/marchio.png", UriKind.Relative);
                    default:
                        throw new InvalidOperationException("Unknown resolution type");
                }
            }
        }

        public Uri BestResolutionImageButtonDown
        {
            get
            {
                switch (ResolutionHelper.CurrentResolution)
                {
                    case Resolutions.HD1080p:
                        return new Uri("../Image/1080p/arrow_down.png", UriKind.Relative);
                    case Resolutions.HD720p:
                        //return 16:9 aspect ratio asset, high res
                        return new Uri("../Image/720p/arrow_down.png", UriKind.Relative);
                    case Resolutions.WXGA:
                        return new Uri("../Image/720p/arrow_down.png", UriKind.Relative);
                    case Resolutions.WVGA:
                        // return 15:9 aspect ratio asset, high res
                        return new Uri("../Image/WVGA/arrow_down.png", UriKind.Relative);
                    default:
                        throw new InvalidOperationException("Unknown resolution type");
                }
            }
        }
        public Uri BestResolutionImageButtonEnter
        {
            get
            {
                switch (ResolutionHelper.CurrentResolution)
                {
                    case Resolutions.HD1080p:
                        return new Uri("../Image/1080p/arrow_enter.png", UriKind.Relative);
                    case Resolutions.HD720p:
                        //return 16:9 aspect ratio asset, high res
                        return new Uri("../Image/720p/arrow_enter.png", UriKind.Relative);
                    case Resolutions.WXGA:
                        return new Uri("../Image/720p/arrow_enter.png", UriKind.Relative);
                    case Resolutions.WVGA:
                        // return 15:9 aspect ratio asset, high res
                        return new Uri("../Image/WVGA/arrow_enter.png", UriKind.Relative);
                    default:
                        throw new InvalidOperationException("Unknown resolution type");
                }
            }
        }
        public Uri BestResolutionImageButtonHome
        {
            get
            {
                switch (ResolutionHelper.CurrentResolution)
                {
                    case Resolutions.HD1080p:
                        return new Uri("../Image/1080p/arrow_home.png", UriKind.Relative);
                    case Resolutions.HD720p:
                        //return 16:9 aspect ratio asset, high res
                        return new Uri("../Image/720p/arrow_home.png", UriKind.Relative);
                    case Resolutions.WXGA:
                        return new Uri("../Image/720p/arrow_home.png", UriKind.Relative);
                    case Resolutions.WVGA:
                        // return 15:9 aspect ratio asset, high res
                        return new Uri("../Image/WVGA/arrow_home.png", UriKind.Relative);
                    default:
                        throw new InvalidOperationException("Unknown resolution type");
                }
            }
        }
        public Uri BestResolutionImageButtonUp
        {
            get
            {
                switch (ResolutionHelper.CurrentResolution)
                {
                    case Resolutions.HD1080p:
                        return new Uri("../Image/1080p/arrow_up.png", UriKind.Relative);
                    case Resolutions.HD720p:
                        //return 16:9 aspect ratio asset, high res
                        return new Uri("../Image/720p/arrow_up.png", UriKind.Relative);
                    case Resolutions.WXGA:
                        return new Uri("../Image/720p/arrow_up.png", UriKind.Relative);
                    case Resolutions.WVGA:
                        // return 15:9 aspect ratio asset, high res
                        return new Uri("../Image/WVGA/arrow_up.png", UriKind.Relative);
                    default:
                        throw new InvalidOperationException("Unknown resolution type");
                }
            }
        }

    }

}