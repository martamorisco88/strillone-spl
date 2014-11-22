using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using System.Diagnostics;
using System.Threading.Tasks;
using strillone.Presenter;
using strillone.Resources;
using strillone.View.Data;
using strillone.View.Interface;

namespace strillone
{
    public partial class ExtendedSplashScreen : PhoneApplicationPage
    {

        public ExtendedSplashScreen()
        {
            InitializeComponent();
            Splash_Screen();
        }

        async void Splash_Screen()
        {
            InitializePresenter ip = new InitializePresenter();

            var response = await ip.checkInternetConnection(); 

            if (response)
            {
                NavigationService.Navigate(new Uri("/View/MainPage.xaml", UriKind.Relative)); // Chiama MainPage
            }
            else
            {
                await Task.Delay(TimeSpan.FromSeconds(1));
                this.setVisibility(false);
                this.tboxs.Visibility = System.Windows.Visibility.Visible;
            }

        }

        public void setVisibility(bool val)
        {
            if (val)
            {
                loadingPanel.Visibility = System.Windows.Visibility.Visible;
            }
            else
            {
                loadingPanel.Visibility = System.Windows.Visibility.Collapsed;
            }
        }

    }
}