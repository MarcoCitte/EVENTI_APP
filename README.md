EVENTI BICOCCA: https://www.unimib.it/eventi E https://ibicocca.unimib.it/eventi/
EVENTI BICOCCA VILLAGE: https://www.bicoccavillage.it/eventi-2/
EVENTI PIRELLI HANGAR: https://pirellihangarbicocca.org/evento/
EVENTI CUSBICOCCA: http://www.cusbicocca.it/eventi/
EVENTI BICOCCA VILLAGE: https://www.bicoccavillage.it/
EVENTI UCI: https://www.ucicinemas.it/cinema/lombardia/milano/uci-cinemas-bicocca-milano/
EVENTI ARCIMBOLDI: https://www.teatroarcimboldi.it/all/
EVENTI CINETECA: https://www.cinetecamilano.it/films E https://www.cinetecamilano.it/attivita/visita-il-mic
EVENTI MILANO IN GENERALE: https://www.milanotoday.it/eventi/

LIBRERIA SCRAPING: JSOUP

ROOM:
Nella classe Events che serve per il parsing con retrofit di un JSON in oggetto JAVA, posso mettere degli attributi aggiuntivi e specificarli come Colonne per il database locale
Room. Per salvare gli eventi come preferiti è da fare in questo modo. La classe Events diventa perciò @Entity.
EventsDao è l'interfaccia che contiene le query SQL da fare al database locale.
EventsRoomDatabase ha l'executor per eseguire una chiamata in un thread a parte al database room.

LIVE DATA:
Due classi astratte per i dati locali e remoti. Quella remota va estesa da un classe EventsRemoteDataSource che avrà i metodi per fare le query (con retrofit) quindi getEvents che contiene onResponse e onFailure etc.
Flusso:(se uso dati remoti)
1)fetchEvents dell'EventsRepository dove con un if sceglie se usare la classe dei dati locali (EventsLocalDataSource) oppure dei dati remoti(EventsRemoteDataSource)
2)viene eseguito il getEvents di EventsRemoteDataSource (che è implementato in entrambi le classi precedenti)
3)se va a buon fine viene chiamato l'onSuccess che a sua volta chiama l'onSuccessOnRemote presente nella classe EventsRepositoryWithLiveData
4)quest'ultimo metodo salva i dati nell'EventsLocalDataSource cioè nel database room richiamando un altro metodo presente in EventsLocalDataSource
5)quest'ultimo metodo chiama l'onSuccessFromLocal che notifica ai fragment che c'è stato un cambiamento sui dati.

VIEW MODEL:
serve per gestire e salvare lo stato dei fragment e activity come l'onSaveInstanceState. Componente tra interfaccia grafica(fragment e activity) e il data layer(repository).
Salviamo le informazioni in questo strato per mantenerle in caso di cambiamenti di stato dell'APP. 
Bisogna creare una classe EventsViewModel che estende AndroidViewModel e si instanzia (new ViewModelProvider) nell'onCreate del fragment grafico.

