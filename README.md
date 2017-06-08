# HorstAI
Teamprojekt um eine KI für Snake zu programmieren

##Verwendete Algorithmen:
-Alpha-Beta Pruning
-HamiltonPathfinding(wie auch immer der Algo. richtig heißt)
-A*-Pathfinding Algorithmus

##A*-Pathfinding Algorithmus
Um den kürzesten Weg zum Ziel zu berechnen verwenden wir den A* Algorithmus. Dieser läuft mit einer openList und 
einer closedList. Die openList ist als PriorityQueue implementiert, damit wir an erster Stelle immer den besten nächsten Point haben und die Liste nicht selbst sortieren müssen. Haben wir den Weg gefunden, können wir anhand der closedList den genauen Pfad vom Ziel zurück zum Start bestimmen durch Rekursion.
