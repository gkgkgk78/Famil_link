using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading.Tasks;

namespace FamilLinkProject.Core
{
    public class MovieData
    {
        private static List<long> movieList = new List<long>();

        public static void setMovieList(List<long> _list)
        {
            movieList = _list;
        }

        public static long popMovie()
        {
            if (movieList.Count == 0)
                return -1;

            var item = movieList[0];
            movieList.RemoveAt(0);

            return item;
        }
    }
}
