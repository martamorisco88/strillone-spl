using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Diagnostics;

namespace strillone.Presenter
{
    class InitializePresenter
    {
        public InitializePresenter()
        {
            Debug.WriteLine("Called constructor initializePresenter");
        }

        public async Task<bool> checkInternetConnection()
        {
            MainPresenter mainPresenter = new MainPresenter();

            var response = await mainPresenter.checkInternetConnection();

            return response;
        }
    }
}
