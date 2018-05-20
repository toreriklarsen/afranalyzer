**Afranalyzer requirements**

* Egne filtre/options for autotune
* Application exit: https://docs.spring.io/spring-boot/docs/2.0.2.RELEASE/reference/htmlsingle/#boot-features-application-exit
* Statistics in gui
* Vise tabell bestanding
* Data browser m tabell. Mekanismer for
    * Mekanismer for å finne overrun, quickgearshift osv.
    * Rare verdier AFR, søk?
    * utføre søk, vurder elastic
    * Tab comparing
    *
* save config/project yml? spring?

* Kompensere for EGO, 2 metoder: 1 fast rpm
* Mere tester på EGO, har gjort en del, forbedret algoritme. Støtter 50Hz oppløsning
* Smooth autotune percent values, hvordan smoothe?
* Transition filter for endringer i TPS eller RPM
* GPS decelleration filter, longitudal accelleration filter, less than = or less than 0,25
* Verify afr cell averaging
* Verify afr cell binning, and should some be excluded?
* Tester
    * Calculering
    * Service
    * CVS fil lesing, smart header parsing
    * Hele løypa, med Inndata, modeller, tuning og tuning result

* Cell info in gui
* Gui edit target file
* Separator bug target file (Jackson)
* Bruk Jackson bedre, mer tolerant
* CSV optional columns, ECT og Longitudal accelleration
* Gjør gui mer sexy, farger og div
* rpm og tps interval bør komme fra target fila
* Lese andre formater + et generelt
* TPS headers
* All double == should do Math.abs(a - b) < epsilon, where epsilon = 0.00000001d;

* Porte til JavaFX?
* Test CSV parsing

**Bugs**
* Values in fields should update when lost focus, not by enter

**Forslag**
Hvordan vise forbedringer av tuningen? Sammenlikning?




 




