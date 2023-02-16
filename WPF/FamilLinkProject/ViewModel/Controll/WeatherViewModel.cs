using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FamilLinkProject.ViewModel.Controll
{
    public class WeatherViewModel
    {
        private static WeatherViewModel instance;
        public static WeatherViewModel GetInstance()
        {
            if (instance == null)
            {
                instance = new WeatherViewModel();
            }
            return instance;
        }


    }
}
