using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

//get e set su title e body di articolo

namespace strillone.Model
{
    public class Articolo
    {
        private String title;
        private String body;
        
        public String getTitle()
        {
            return this.title;
        }

        public String getBody()
        {
            return this.body;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public void setBody(String body)
        {
            this.body = body;
        }
    }
}
