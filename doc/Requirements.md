**Afranalyzer requirements**

* Egne filtre/options for autotune
* Application exit: https://docs.spring.io/spring-boot/docs/2.0.2.RELEASE/reference/htmlsingle/#boot-features-application-exit
* Statistics in gui
* Distribuer i lzpack
* Vise tabell bestanding?
* Rendre celler med int's (ta vekk flytall)
* 3D visualisering av map og ev. afr verdier
* Data browser m tabell. Mekanismer for
    * For å finne overrun, quickgearshift osv.
    * Gearshift (quickshifter)
    * Rare verdier AFR, søk?
    * utføre søk, vurder elastic
    * Tab comparing
    * 
* save config/project yml? spring?
* Disable filtere som ikke er relecvant (ikke har data)
* Kompensere for EGO, 2 metoder: 1 fast rpm
* Mere tester på EGO, har gjort en del, forbedret algoritme. Støtter 50Hz oppløsning
* Smooth autotune percent values, hvordan smoothe?
* Transition filter for endringer i TPS eller RPM
* Gear filter, så det er mulig å se verdier per gear, kun for analyse
* Verify afr cell averaging
* Verify afr cell binning, and should some be excluded?
* Tester
    * Calculering
    * AutoTuneService 1 pri
    * CVS fil lesing, smart header parsing
    * Nye CSV Parser
    * Hele løypa, med Inndata, modeller, tuning og tuning result

* Cell info in gui
* Gui edit target file
* Target file som json eller yml? Her kan relevant cell settes
* Separator bug target file (Jackson)
* Gjør gui mer sexy, farger og div
* rpm og tps interval bør komme fra target fila, eller noe annet sted
* TPS headers
* All double == should do Math.abs(a - b) < epsilon, where epsilon = 0.00000001d;


**Bugs**
* Values in fields should update when lost focus, not by enter
* Fjern rad TPS 0
* Fjern colonne RPM 0 og 1000, foreløpig kun i visning


**Forslag**
Hvordan vise forbedringer av tuningen? Sammenlikning?
Hvordan bruke AI eller maskinlæring? Sjekk dette




 




