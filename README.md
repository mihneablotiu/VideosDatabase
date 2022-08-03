Bloțiu Mihnea-Andrei - 323CA
VideosDB - Tema - 29.11.2021

Pentru rezolvarea acestei teme am creat o bază de date „Database” ce este
folosită drept punct de plecare pentru fiecare operație solicitată, mai exact
prin metoda interrogate.

Aceasta conține:
 - Lista cu toți utilizatorii;
 - Lista cu toate filmele;
 - Lista cu toate serialele;
 - Lista cu toți actorii;
 - Lista cu toate acțiunile ce urmează a fi executate;
 - JSONArray-ul ce va conține răspunsul final.

Toate acestea sunt citite din clasa Main prin intermediul claselor prestabilite
ce parsează datele de intrare după cum urmează:
 - Se crează o listă cu toate datele de intrare din același tip
 - Pentru fiecare element din acea listă se creează entități ale claselor similare,
custom, intitulate (User, Actor, Action, Movie și Serial). Aceste conțin exact
aceleași câmpuri ca și clasele ce parsează input-ul însă le vom folosi pe acestea
în cele ce urmează pentru nu strica citirea datelor.
 - Se crează un obiect Database cu noile liste de enitați ale claselor custom
și se aplează metoda interrogate.

Metoda interrogate iterează prin ficare acțiune primită și nu face altceva
decât să aleagă acțiunea ce urmează a fi executată.

În cazul celor trei instrucțiuni de tip „command”, implementările acestora
au fost făcute în clasa custom „User” deoarece funcționalitățile depind de
fiecare utilizator în parte după cum urmează:

Metoda favorite:
 - Verifică dacă filmul/serialul a fost văzut de către utilizator
 - Il adăugăm în lista de favorite în funcție de răspuns și afișăm
mesajul corespunzător.

Metoda view:
 - Verifică dacă filmul/serialul a fost deja văzut de către utilizator
și îl adaugă în listă sau îi crește numărul de vizualizări în funcție de
situație.

Metodele rateMovie și rateSerial:
 - Oferă o notă filmului/serialului corespunzător în funcție de restricțiile
specifice cu diferența că rating-ul unui film va fi considerat pe tot filmul
în schimb ce pentru un serial se poate da o dată pentru fiecare sezon.

În cazul celor opt instrucțiuni de tip „query”, implementările acestora
au fost făcute în clasa custom „Query” deoarece funcționalitățile depind de
la situație la situație, nu sunt specifice niciunei clase deja menționate:

Metodele Average, Awards, Rating, Favorite, Longest, mostViewed, mostActive:
 - Am creat câte o clasă nouă pentru fiecare dintre acestea, 
în care entitățile rețin atât numele actorului/filmului/serialului de care avem
nevoie pentru a le afișa în JSONArrray cât și criteriul principal după care au fost sortate
precum (ratingul primit / lungimea filmului / serialului / numărul apariției în listele
de favorite / etc.).
 - Se sortează o listă cu toate elementele acestei clase nou creată pe baza unuia dintre
criteriile menționate anterior, iar apoi alfabetic.
 - Se afișează primele N elemente din lista sortată sau toate dacă sunt mai
puține elemente decât numărul cerut.

Metoda filterDescription:
 - Se caută toți actorii din lista de actori ce conțin toate cuvintele date însă
pe baza unui pattern deoarece ne dorim ca acele cuvinte să fie de sine stătătoare
în cadrul descrierii și nu să fie substringuri în cadrul altor cuvinte mai mari.
 - Se sortează această listă alfabetic după criteriul dat în acțiunea curentă.

În cazul celor cinci instrucțiuni de tip „Recommendations”, implementările acestora
au fost făcute în clasa custom „Recommendation” deoarece funcționalitățile depind de
la situație la situație, nu sunt specifice niciunei clase deja menționate:

Metoda standardRec:
 - Parcurge filmele și serialele în această ordine, adică în cea din baza de date
și îl întoarce pe primul ce nu a fost văzut de utilizator.

Metoda bestUnseen:
 - Identică din punct de vedere al funcționalității cu cea anterioară însă se
calculează rating-ul pentru fiecare film și serial în parte și se sortează mai
întâi în funcție de acest aspec și apoi se ține cont de ordinea din baza de date.

Metodele popular, favorite și search au același mod de funcționare, sortând
toate elementele după criteriile menționate în cerință însă se verifică și
tipul utilizatorului deoarece acestea nu se pot aplica decât celor „premium”.