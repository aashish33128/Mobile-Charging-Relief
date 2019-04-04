import csv
import re

#Reads the data file and produces output file required for the algorithm
def readData(listofKeys):
	c = -1;
	nodesDict = {};
	nodesLevels= [];
	currentKey = '';
	
	with open('data.txt') as file:
		for line in file:
			pattern = re.compile(".*en_US.*");
			if(pattern.match(line)):
				nodeId = line.split(';')[0].split('.')[0];
				c = c + 1;
				listofKeys.append(nodeId);
				if(c!=0):
					nodesDict[currentKey] = nodesLevels;

				currentKey = nodeId;
				nodesLevels= [];
			else:
				chargingLevel = line.split(';')[4].rstrip('\n');
				nodesLevels.append(chargingLevel);
	nodesDict[currentKey] = nodesLevels;
	print('----Finished Reading Files and Extracting Data------');
	print('Total Size of nodes',  len(nodesDict));
	
	return nodesDict;

def getChargingCycles(nodesLevels):

	
	firstLevel = int(nodesLevels[0]);
	levels= [];
	orderChanged = False;
	levels.append(firstLevel);
	#print(len(nodesLevels)-1);
	for i in range(1,len(nodesLevels)-1):
		curLevel = int(nodesLevels[i]);
		nextLevel = int(nodesLevels[i+1]);
		#print('cur Level and next level is ',firstLevel,  curLevel, nextLevel, orderChanged);

		if(curLevel < firstLevel and nextLevel > curLevel):
			orderChanged = True;
		elif(curLevel > firstLevel and nextLevel < curLevel):
			orderChanged = True;
		else:
			orderChanged = False;
		if(i==len(nodesLevels)-2):
			levels.append(nextLevel);
		if(curLevel == firstLevel or nextLevel == curLevel):		
			continue;
			

		if(orderChanged):
			if(abs(int(levels[len(levels)-1]) - int(curLevel)) > 4):
				levels.append(curLevel);
				
			else:
				levels[len(levels)-1] = curLevel;
				
		firstLevel = curLevel;
				
		
		
			
	

	return levels;
	
			




def getAllNodes(nodesDict, listofKeys):
	
	allNodes={};
	
	for i in range(0,len(listofKeys)):
		levels = getChargingCycles(nodesDict[listofKeys[i]]);
		if(len(levels) > 0):
				
			allNodes[listofKeys[i]] = levels;
			
	
	return allNodes;


def writeToFile(acceptedNodes,listofKeys):

	
	with open("output.csv", "w") as f:
		writer = csv.writer(f);
		for key in acceptedNodes:
			levels = acceptedNodes[key];
			writer.writerow(levels);
			
def reFilterList(chargingValues):
	prev = chargingValues[0];
	#print("Prev is ", prev);
	finalCycle = [];
	finalCycle.append(prev);
	for i in range(1, len(chargingValues)-1):
		cur = chargingValues[i];
		next = chargingValues[i+1];
		#print('cur Level and next level is ',prev,  cur, next);
		if(((int(cur)+5) < int(prev) and int(next) > int(cur)) or (int(cur)>(int(prev)+5) and int(next) < int(cur))):
			finalCycle.append(cur);
		if(i == len(chargingValues)-2):
				finalCycle.append(next);
		prev = cur;
	return finalCycle;
			
def main():
	listofKeys = [];
	acceptedNodes = {};
	nodesDict = readData(listofKeys);
	allNodes = getAllNodes(nodesDict,listofKeys);
	
	for key in allNodes:
		finalList = reFilterList(allNodes[key]);
		if(len(finalList) > 3):
			acceptedNodes[key] = finalList;
	print("Total Accepted Nodes are : ", len(acceptedNodes))
	writeToFile(acceptedNodes,listofKeys);
	#Print to see the values
	#for key in acceptedNodes:
		#print('For Mobile Node : ', key);
		#print('\n values are \n');
		#for values in acceptedNodes[key]:
			#print(values);


if __name__=="__main__":
	main();


			
