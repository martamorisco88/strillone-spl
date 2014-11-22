using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using strillone.View.Data;

namespace strillone.View.Interface
{
    public interface IMainView
    {
        void InitializeView(MainViewData viewModel);
    }
}
