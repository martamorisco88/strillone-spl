using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.IO;

using System.Xml;
using System.Xml.Linq;
using WinPhoneExtensions;

using strillone.Presenter.Interface;
using strillone.View.Data;
using strillone.View.Interface;

using Windows.Phone.Speech.Synthesis;
using Windows.Foundation;

using Microsoft.Phone.Net.NetworkInformation;

using strillone.Model;

using System.Resources;
using System.Threading;
using System.Windows;

// http://stackoverflow.com/questions/16726362/reading-node-info-using-xdocument-windows-phone 

namespace strillone.Presenter
{
    public class MainPresenter : IMainPresenter
    {
        private MainViewData _viewModel;
        private IMainView _view;

        private StreamReader readStream;

        private String CurrentNavigation;
        private int CurrentNavigationIndex;

        private bool navigationEnabled = false;
        private bool disableAll = false;

        private bool isLastElement;
        private bool isFirstElement;
        private bool isUnique;

        private String deviceLanguage;

        //per interrompe il tts quando premo un nuovo tasto
        private IAsyncAction task;

        XDocument sezioni = new XDocument();

        List<String> levelList = new List<String>();
        List<object> testateList = new List<object>();
        List<object> sezioniList = new List<object>();
        List<object> articoliList = new List<object>();

        Testata currT = new Testata();
        Sezione currS = new Sezione();
        Articolo currA = new Articolo();

        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer();

        MainPage mp;


        private String mainURL = "http://www.walks.to/strillonews/newspapers";
       

        // Get e Set su CurrentNavigation e CurrentNavigationIndex
        public String getCurrentNavigation()
        {
            return this.CurrentNavigation;
        }

        public void setCurrentNavigation(String currentNavigation)
        {
            this.CurrentNavigation = currentNavigation;
        }

        public int getCurrentNavigationIndex()
        {
            return this.CurrentNavigationIndex;
        }

        public void setCurrentNavigationIndex(int CurrentNavigationIndex)
        {
            this.CurrentNavigationIndex = CurrentNavigationIndex;
        }

        public MainPresenter(IMainView view)
        {
            _view = view;    
        }

        //Riferimento per collegare gli elementi di MainPage al MainPresenter
        public MainPresenter(MainPage mp)
        {           
            this.mp = mp;
        }

        //Viene invocato da InizializePresenter
        public MainPresenter()
        {
        }


        //Inizializzazione, leggo url, leggo testata e i relativi nodi figli
        public async void InitializeView()
        {                         
            this.deviceLanguage = this.formatLanguageString(System.Threading.Thread.CurrentThread.CurrentCulture.ToString());
            levelList.Add("testate");
            levelList.Add("sezioni");
            levelList.Add("articoli");

            this.setCurrentNavigation("testate");
            this.setCurrentNavigationIndex(-1);

            //navigo l'xml
            XDocument testate = await this.readXML(this.mainURL);

            if (testate != null)
            {
                
                this.testateList.Clear();

                foreach (var stop in testate.Root.Elements("testata"))
                {
                    var nome = stop.Element("nome") != null ? stop.Element("nome") : null;
                    var resource = stop.Element("resource") != null ? stop.Element("resource") : null;
                    var edizione = stop.Element("edizione") != null ? stop.Element("edizione") : null;
                    var lingua = stop.Element("lingua") != null ? stop.Element("lingua") : null;
                    var beta = stop.Element("beta") != null ? stop.Element("beta") : null;

                    if (nome == null || resource == null || edizione == null || lingua == null)
                    {
                        Debug.WriteLine("Testata non valida: nome, URL, edizione o lingua sono mancanti");
                        continue;
                    }
                    else
                    {
                        Debug.WriteLine(nome + " " + resource + " " + edizione + " " + lingua);
                    }

                      if (beta == null || Convert.ToBoolean(beta.Value) != true)
                      {
                        Debug.WriteLine(lingua.Value + "    -    " + this.deviceLanguage);
                        if (lingua.Value == this.deviceLanguage)
                        {
                            Testata testata = new Testata();
                            testata.setName(nome.Value);
                            testata.setEdition(edizione.Value);
                            testata.setURL(resource.Value);

                            this.testateList.Add(testata);
                        }
                    }
                    else
                    {
                        Debug.WriteLine(nome.Value + " è BETA!");
                    }

                }
                if (this.testateList.Count < 1)
                {
                    //this.ttsRead("Non ci sono testate nella tua lingua");
                    this.ttsRead(strillone.Resources.AppResources.noTitles);
                    this.disableAll = true;
                }
            }
            await Task.Delay(TimeSpan.FromSeconds(2));
            this.mp.tbox1.Visibility = System.Windows.Visibility.Collapsed;
        }


        // navigazione tasto dex-sopra
        public void navigatePrev()
        {
            String prepend = strillone.Resources.AppResources.SuggestionBtnPrecedente + ". ";
            String stringToRead = "";

            //funzionalità utili per la navigazione
            if (this.disableAll)
            {
                return;
            }
            //this.isFirstElement = false;
            /*per ABILITARE il bottone Prev solo se:
             - la navigazione è abilitata
             - se sei all'interno della sezione testo ovvero solo se stai leggendo un articolo e vuoi passare al precedente 
               (o successivo)*/
            //ho modificato cnavigation con testo invece che articoli
            if (!this.navigationEnabled && this.CurrentNavigation != "testo")
            {
                //Funzionalità utili per il TTS
                switch (this.getCurrentNavigation())
                {
                    case "testate":
                        stringToRead = strillone.Resources.AppResources.SuggestionOnTitle;
                        break;

                    case "sezioni":
                        stringToRead = strillone.Resources.AppResources.SuggestionOnSection;
                        break;

                    case "articoli":
                        stringToRead = strillone.Resources.AppResources.SuggestionOnArticle;
                        break;
                }

                this.ttsRead(prepend + stringToRead);
                return;
            }

            if (this.CurrentNavigation == "testo")
            {
                this.CurrentNavigation = "articoli";
            }

            this.navigationEnabled = true;


            switch (this.getCurrentNavigation())
            {
                case "testate":                   
                    this.CurrentNavigationIndex = this.getPrevElem(this.testateList, this.CurrentNavigationIndex);

                    if (!isFirstElement)
                    {
                        this.currT = (Testata)this.testateList[this.CurrentNavigationIndex];
                        stringToRead = this.currT.getName() + " " + formatDate(this.currT.getEdition());
                        // se isUnique è true cambiamo isFirst a true poiechè c'è un solo elemento
                        if (this.isUnique)
                        {
                            this.isFirstElement = true;
                        }
                    }
                    else
                    {
                        stringToRead = strillone.Resources.AppResources.SuggestionOnPositionTestata;
                    }

                    break;

                case "sezioni":
                    this.CurrentNavigationIndex = this.getPrevElem(this.sezioniList, this.CurrentNavigationIndex);
                    // se non è il primo elemento legge il testo
                    if (!isFirstElement)
                    {
                        this.currS = (Sezione)this.sezioniList[this.CurrentNavigationIndex];
                        stringToRead = this.currS.getName();
                        if (this.isUnique)
                        {
                            this.isFirstElement = true;
                        }
                    }
                    else
                    {
                        stringToRead = strillone.Resources.AppResources.SuggestionOnPositionSezione;
                    }

                    break;

                case "articoli":
                case "testo":                 
                    this.CurrentNavigationIndex = this.getPrevElem(this.articoliList, this.CurrentNavigationIndex);

                    if (!isFirstElement)
                    {
                        this.currA = (Articolo)this.articoliList[this.CurrentNavigationIndex];
                        stringToRead = this.currA.getTitle();
                        if (this.isUnique)
                        {
                            this.isFirstElement = true;
                        }
                    }
                    else
                    {
                        stringToRead = strillone.Resources.AppResources.SuggestionOnPositionArticolo;
                    }

                    break;
            }
            this.ttsRead(prepend + stringToRead);
        }

        // navigazione tasto dex-sotto
        public void navigateNext()
        {
            String prepend = strillone.Resources.AppResources.SuggestionBtnSuccessivo + ". ";
            String stringToRead = "";

            if (this.CurrentNavigation == "testo")
            {
                this.CurrentNavigation = "articoli";
            }

            //funzionalità utili per la navigazione
            if (this.disableAll)
            {
                return;
            }

            this.navigationEnabled = true;


            switch (this.getCurrentNavigation())
            {
                case "testate":

                    this.CurrentNavigationIndex = this.getNextElem(this.testateList, this.CurrentNavigationIndex);

                    if (!isLastElement)
                    {
                        this.currT = (Testata)this.testateList[this.CurrentNavigationIndex];
                        stringToRead = this.currT.getName() + " " + formatDate(this.currT.getEdition());
                        if (this.isUnique)
                        {
                            this.isLastElement = true;
                        }
                    }
                    else
                    {
                        stringToRead = strillone.Resources.AppResources.SuggestionOnPositionLastTestata;
                    }

                    break;

                case "sezioni":

                    this.CurrentNavigationIndex = this.getNextElem(this.sezioniList, this.CurrentNavigationIndex);

                    if (!isLastElement)
                    {
                        this.currS = (Sezione)this.sezioniList[this.CurrentNavigationIndex];
                        stringToRead = this.currS.getName();
                        if (this.isUnique)
                        {
                            this.isLastElement = true;
                        }
                    }
                    else
                    {
                        stringToRead = strillone.Resources.AppResources.SuggestionOnPositionLastSezione;
                    }

                    break;

                case "articoli":
                case "testo":
                    this.CurrentNavigationIndex = this.getNextElem(this.articoliList, this.CurrentNavigationIndex);

                    if (!isLastElement)
                    {
                        this.currA = (Articolo)this.articoliList[this.CurrentNavigationIndex];
                        stringToRead = this.currA.getTitle();
                        if (this.isUnique)
                        {
                            this.isLastElement = true;
                        }
                    }
                    else
                    {
                        stringToRead = strillone.Resources.AppResources.SuggestionOnPositionLastArticolo;
                    }

                    

                    break;
            }

            this.ttsRead(prepend + stringToRead);
        }


        // navigazione tasto sin-sotto
        public async void navigateEnter()
        {
            String prepend = strillone.Resources.AppResources.SuggestionBtnEntra + ". ";
            String stringToRead = "";
            this.mp.tbox.Text = "Lettura " + "\"" + this.currT.getName() + "...\"";
            this.mp.progressBar1.Visibility = System.Windows.Visibility.Visible;

            if (this.disableAll)
            {
                return;
            }
            if (!this.navigationEnabled)
            {
                //funzionalità utili per il TTS
                switch (this.getCurrentNavigation())
                {
                    case "testate":
                        stringToRead = strillone.Resources.AppResources.SuggestionOnTitle;
                        break;

                    case "sezioni":
                        stringToRead = strillone.Resources.AppResources.SuggestionOnSection;
                        break;

                    case "articoli":
                    case "testo":
                        stringToRead = strillone.Resources.AppResources.SuggestionOnArticle;
                        break;
                }

                this.ttsRead(prepend + stringToRead);

                return;
            }

            switch (this.getCurrentNavigation())
            {
                //quando sei in testate inizializza sezioni (cronaca, politica, ecc)
                case "testate":
                    this.setCurrentNavigation("sezioni");
                    if (await this.initializeSezioni())
                    {
                        //Il TTS notifica se la testata e pronta per la lettura
                        stringToRead = this.currT.getName() + " " + strillone.Resources.AppResources.TestataReady;
                        // current navig viene settato a -1  per poter prendere il primo
                        // item della navigazione corrente sia quando si fa avanti che indietro
                        this.setCurrentNavigationIndex(-1);
                        this.isFirstElement = false;
                        this.isLastElement = false;
                    }
                    else
                    {
                        //Il TTS notifica se vi è errore di connessione con il server
                        stringToRead = strillone.Resources.AppResources.ErrorServer;
                        this.navigateExit(true);
                    }
                    
                    break;

                //quando sei in sezioni inizializza articoli
                case "sezioni":
                    this.setCurrentNavigation("articoli");
                    this.initializeArticoliBySezione();
                    //Il TTS notifica in quale sezione si sta entrando
                    stringToRead = strillone.Resources.AppResources.EnterSezione + " " + this.currS.getName();
                    // current navig viene settato a -1  per poter prendere il primo
                    // item della navigazione corrente sia quando si fa avanti che indietro
                    this.setCurrentNavigationIndex(-1);
                    this.isFirstElement = false;
                    this.isLastElement = false;
                    break;

                //quando sei in articoli leggi testo articolo
                case "articoli":
                    this.setCurrentNavigation("testo");
                    stringToRead = this.currA.getBody();
                    break;
            }

            this.ttsRead(prepend + stringToRead);
            this.navigationEnabled = false;
        }

        // navigazione tasto sin-sopra
        // se forziamo la chiamata navigateExit(true) il tts non parla
        public void navigateExit(bool forced=false)
        {
            String prepend = strillone.Resources.AppResources.SuggestionBtnSali + ". ";
            String stringToRead = "";

            this.CurrentNavigationIndex = -1;
            this.isFirstElement = false;
            this.isLastElement = false;

            //funzionalità utili per la navigazione
            if (this.disableAll) 
            {
                return;
            }

            switch (this.getCurrentNavigation())
            {
                //se è testate vai a inizio navigazione
                case "testate":
                    if (!forced)
                    {
                        //Il TTS notifica che ci si trova su inizio navigazione
                        stringToRead = strillone.Resources.AppResources.StartNavigazione;
                    }
                    break;

                //se è sezioni vai a navigazione testate
                case "sezioni":
                    this.setCurrentNavigation("testate");
                    if (!forced)
                    {
                        //Il TTS notifica che ci si trova su navigazione edizioni
                        stringToRead = strillone.Resources.AppResources.StartEdizioni;
                    }
                    break;

                //se è articoli vai a navigazione sezioni
                case "articoli":
                case "testo":
                    this.setCurrentNavigation("sezioni");
                    if (!forced)
                    {
                        //Il TTS notifica che ci si trova su inizio sezioni
                        stringToRead = strillone.Resources.AppResources.StartSezioni;
                    }
                    break;
            }

            this.ttsRead(prepend + stringToRead);

            this.setCurrentNavigationIndex(-1);
            this.navigationEnabled = false;
        }

        //mando una richiesta http per leggere l'xml
        async Task<bool> getXMLfromURL(String url)
        {
            try
            {
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
                request.Method = HttpMethod.Get;
                HttpWebResponse response = (HttpWebResponse)await request.GetResponseAsync(); 

                Stream receiveStream = response.GetResponseStream();

                this.readStream = new StreamReader(receiveStream, Encoding.GetEncoding("iso-8859-1"));
                return true;

            }
            catch(System.Net.WebException e)
            {
                Debug.WriteLine("errore nel caricamento dei dati");
            }

            return false;
        }

        //Leggo l'XML da server
        private async Task<XDocument> readXML(String urlXML)
        {            
            if (!this.checkConnectionAvailable())
            {
                this.ttsRead(strillone.Resources.AppResources.ErrorServer);
                await Task.Delay(TimeSpan.FromSeconds(3));
                Application.Current.Terminate();
                return null;

            }

            try
            {
                if (this.mp != null)
                {
                    this.mp.setVisibility(true);
                    // blocco GUI
                    this.disableAll = true;

                }
                bool docReady = await this.getXMLfromURL(urlXML);

                XDocument xdoc = new XDocument();

                if (docReady)
                {
                    try
                    {
                        xdoc = XDocument.Load(this.readStream);
                        if (this.mp != null)
                        {
                            this.mp.setVisibility(false);
                            // sblocco GUI
                            this.disableAll = false;
                        }
                            return xdoc;
                    }
                    catch(System.NullReferenceException e)
                    {
                        Debug.WriteLine("errore nel caricamento dati");
                        
                    }                    
                }
                return null;
                
            }
            catch (System.Net.WebException e)
            {
                Debug.WriteLine("errore di WebException");
                return null;
            }

           
        }
        /*Metodo utilizzato per la lettura del TTS e per interrompere il TTS quando
        sim preme un'area diversa dell'applicazione*/
        public void ttsRead(String text)
        {
            Debug.WriteLine("-");
           
            try
            {
                if (task != null)
                {
                     task.Cancel();
                }
            }
            catch (Exception e)
            {
                Debug.WriteLine("task is null Exception");
            }

            try
            {
                //tts legge il testo
                task = this.speechSynthesizer.SpeakTextAsync(text);
                Debug.WriteLine(text);
            }
            catch (Exception e)
            {
            }

        }

        //Format per leggere il mese in edizioni
        public String formatDate(String date)
        {
            String[] months = new String[] { strillone.Resources.AppResources.Mese1, strillone.Resources.AppResources.Mese2, strillone.Resources.AppResources.Mese3, strillone.Resources.AppResources.Mese4, strillone.Resources.AppResources.Mese5, strillone.Resources.AppResources.Mese6, strillone.Resources.AppResources.Mese7, strillone.Resources.AppResources.Mese8, strillone.Resources.AppResources.Mese9, strillone.Resources.AppResources.Mese10, strillone.Resources.AppResources.Mese11, strillone.Resources.AppResources.Mese12 };
            String[] dateFormatted = date.Split('-');

            int indexMonth = Convert.ToInt16(dateFormatted[1]) - 1;

            int giorno = Convert.ToInt32(dateFormatted[2]);

            return strillone.Resources.AppResources.EditionStamp + " " + giorno + " " + months[indexMonth] + " " + dateFormatted[0];
        }

        //Inizializzazione di sezioni con il relativo nodo figlio nome
        public async Task<bool> initializeSezioni()
        {
            //var usata per settare il valore bool di return
            bool returnValue = false;

            //var usata per controllare se ci sono sezioni 
            bool isEmpty = true;

            this.sezioni = await this.readXML(this.mainURL + "/" + this.currT.getURL());

            //funzionalità per il controllo della navigazione
            if (this.sezioni != null)
            {
                this.sezioniList.Clear();

                foreach (var stop in sezioni.Root.Elements("sezione"))
                {
                    var nome = stop.Element("nome") != null ? stop.Element("nome") : null;

                    if (nome == null)
                    {
                        Debug.WriteLine("Nome della sezione mancante");
                        continue;
                    }

                    isEmpty = false;
                    returnValue = true;

                    Sezione sezione = new Sezione();
                    sezione.setName(nome.Value);
                    Debug.WriteLine("Aggiunta sezione " + nome.Value);
                    this.sezioniList.Add(sezione);
                }
            }
            else
            {
                returnValue = false;
            }

            if (isEmpty)
            {
                returnValue = false;
            }

            return returnValue;

        }

        //Inizializzazione di articoli di sezione con i relativi nodi figli titolo e testo
        public void initializeArticoliBySezione()
        {
            //query che prende per ogni sezione gli N articoli che ne fanno parte
            IEnumerable<XElement[]> articoli =
                (from el in this.sezioni.Root.Elements("sezione")
                 where el.Element("nome") != null && (el.Element("nome")).Value == this.currS.getName()
                 select el.Elements("articolo").ToArray());

            this.articoliList.Clear();
           
            //ciclo su ogni articolo su cui ho fatto la queru per prendere i figli titolo e testo
            foreach (XElement[] stop2 in articoli)
            {
                for (int i = 0; i < stop2.Length; i++)
                {
                    var titolo = stop2[i].Element("titolo") != null ? stop2[i].Element("titolo") : null;
                    var testo = stop2[i].Element("testo") != null ? stop2[i].Element("testo") : null;

                    if (titolo == null || testo == null)
                    {
                        Debug.WriteLine("Titolo o testo sono mancanti");
                        continue;
                    }

                    Articolo articolo = new Articolo();
                    articolo.setTitle(titolo.Value);
                    articolo.setBody(testo.Value);

                    this.articoliList.Add(articolo);
                }
            }
        }


        //interruzione quando togliamo la rete prima di entrare in una testata
        public bool checkConnectionAvailable()
        {

            // http://stackoverflow.com/questions/20676816/how-to-check-the-internet-connection-availability-in-windows-phone-8-application


            return (Microsoft.Phone.Net.NetworkInformation.NetworkInterface.NetworkInterfaceType != Microsoft.Phone.Net.NetworkInformation.NetworkInterfaceType.None);
        }

        //controllo connessione
        public async Task<bool> checkInternetConnection()
        {
            bool isAvailable = false;

            XDocument testate = await this.readXML(this.mainURL);

            if (testate != null)
            {
                isAvailable = true;
            }

            return isAvailable;
        }

        public String formatLanguageString(String language)
        {
            String languageFormattedString = "";
            String[] languageFormatted = language.Split('-');

            if (languageFormatted.Length == 2)
            {
                languageFormattedString = languageFormatted[0];
            }

            return languageFormattedString;
        }

        /**
         * restituisce il successivo elemento della lista currList (SE ESISTE) 
         * 
         * */
        public int getNextElem(List<object> currList, int cni)
        {
            // cni sta per Current Navigation Index

            // serve per sapere se nella lista c'è un solo elemento
            this.isUnique = false;

            int updatedNavigationIndex = cni;

            if (currList.Count == 1)
            {
                this.isUnique = true;
                updatedNavigationIndex = 0;
                if (!this.isLastElement)
                {
                    this.isLastElement = false;
                    this.isFirstElement = true;
                }
                else
                {
                    this.isLastElement = true;
                    this.isFirstElement = false;
                }
            }
            else
            {
                if (cni < currList.Count - 1)
                {
                    if (!this.isFirstElement)
                    {
                        updatedNavigationIndex++;
                    }
                    else
                    {
                        this.isFirstElement = false;
                    }
                }
                else
                {
                    this.isLastElement = true;
                }
            }

            return updatedNavigationIndex;
        }


        public int getPrevElem(List<object> currList, int cni)
        {
            int updatedNavigationIndex = cni;
            this.isUnique = false;

            if (currList.Count == 1)
            {
                this.isUnique = true;
                if (!this.isFirstElement)
                {
                    updatedNavigationIndex = 0;
                    this.isFirstElement = false;
                    this.isLastElement = false;
                }
                else
                {
                    updatedNavigationIndex = 0;
                    this.isFirstElement = true;
                    this.isLastElement = false;
                }
            }
            else
            {
                if (cni > 0)
                {
                    if (!this.isLastElement)
                    {
                        updatedNavigationIndex--;
                    }
                    else
                    {
                        this.isLastElement = false;
                    }
                }
                else
                {
                    updatedNavigationIndex = 0;
                    this.isFirstElement = true;
                }
            }

            return updatedNavigationIndex;
        }

        public void AccessHelp()
        {
            this.ttsRead(strillone.Resources.AppResources.Help);
        }

        public void ExitApp()
        {
            this.disableAll = true;
            this.ttsRead(strillone.Resources.AppResources.ExitApp);
        }

        public void NodeSelected()
        {          
            String stringToRead = "";
 
            switch (this.getCurrentNavigation())
            {
                case "testate":
                    if (this.CurrentNavigationIndex < 0)
                    {
                        stringToRead = strillone.Resources.AppResources.NoNodeSelectedTestata;
                    }
                    else
                    {
                        stringToRead = this.currT.getName() + " " + formatDate(this.currT.getEdition());
                    }
                    break;

                case "sezioni":
                    if (this.CurrentNavigationIndex < 0)
                    {
                        stringToRead = strillone.Resources.AppResources.NoNodeSelectedSezione;
                    }
                    else
                    {                       
                        stringToRead = this.currS.getName();
                    }
                    break;

                case "articoli":
                case "testo":
                    if (this.CurrentNavigationIndex < 0)
                    {
                        stringToRead = strillone.Resources.AppResources.NoNodeSelectedArticolo;
                    }
                    else
                    {                        
                        stringToRead = this.currA.getTitle();                       
                    }
                    break;
            }

            this.ttsRead(stringToRead);

        }
    }
}
