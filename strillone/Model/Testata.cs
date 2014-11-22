using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

//Get e set su name, edition e url di testata

namespace strillone.Model
{
    public class Testata
    {
        private String name;
        private String edition;
        private String URL;

        public String getName()
        {
            return this.name;
        }

        public String getEdition()
        {
            return this.edition;
        }

        public String getURL()
        {
            return this.URL;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public void setEdition(String edition)
        {
            this.edition = edition;
        }

        public void setURL(String URL)
        {
            this.URL = URL;
        }
    }
}
