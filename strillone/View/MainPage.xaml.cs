using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Resources;
using System.Windows.Controls;
using Microsoft.Phone.Controls;
using System.Windows.Navigation;
using System.Diagnostics;
using Microsoft.Phone.Shell;
using strillone.Resources;
using strillone.Presenter;
using strillone.View.Data;
using strillone.View.Interface;
using System.Threading.Tasks;
using System.Windows.Media;
using System.Windows.Threading;
using System.Threading;



namespace strillone
{
    public partial class MainPage : PhoneApplicationPage
    {
        // Costruttore
        private MainPresenter mp;

        //Non tornare alla schermata precedente (ExtendedSplashscreen.xaml)
        protected override void OnNavigatedTo(System.Windows.Navigation.NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);

            if (NavigationService.BackStack.Count() == 1)
            {
                NavigationService.RemoveBackEntry();
            }

        }

        public MainPage()
        {
            var media = new MediaElement();
            media.AutoPlay = true;
            media.Source = new Uri("Assets/Sound/jingle.mp3", UriKind.Relative);
            InitializeComponent();
            tbox1.Visibility = System.Windows.Visibility.Visible;
            this.mp = new MainPresenter(this);
            this.mp.InitializeView();
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


        private void ButtonAltoSin_Click(object sender, RoutedEventArgs e)
        {
            Debug.WriteLine("Button AltoSin pressed");
            this.mp.navigateExit();
        }

        private void ButtonAltoDex_Click(object sender, RoutedEventArgs e)
        {
            Debug.WriteLine("Button AltoDex pressed");           
            this.mp.navigatePrev();
        }

        private void ButtonBassoSin_Click(object sender, RoutedEventArgs e)
        {
            Debug.WriteLine("Button BassoSin pressed");
            this.mp.navigateEnter();
        }

        private void ButtonBassoDex_Click(object sender, RoutedEventArgs e)
        {
            Debug.WriteLine("Button BassoDex pressed");
            this.mp.navigateNext();
        }

        private void basso_sin_Hold(object sender, System.Windows.Input.GestureEventArgs e)
        {           
            this.mp.AccessHelp();
        }

        private async void alto_sin_Hold(object sender, System.Windows.Input.GestureEventArgs e)
        {
            this.mp.ExitApp();
            await Task.Delay(TimeSpan.FromSeconds(2));
            Application.Current.Terminate();

        }

        private void alto_dex_Hold(object sender, System.Windows.Input.GestureEventArgs e)
        {
            Debug.WriteLine("Button altoDexHold pressed");
            this.mp.NodeSelected();

        }
      
    }
}