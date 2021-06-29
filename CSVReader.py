import os
import csv

''' Python file intended to be run in a batch script to read and write metrics 
    from CSV files generated from Understand Scitool projects.
'''

print('-- CSVReader.py loaded --')

Projects = []
#2D Array, project[0] = Project name, Project[1] = All files, Project[2] = Test files, Project[3] = Git files


for file in os.listdir('UndProjects'):
    if '.csv' in file and file != 'AllMetrics.csv':
        Projects.append([file[:-4]])
metricHeaders = []
print('Reading Projects...')
for project in Projects:
    
    #Read CSV metrics
    reader = open('UndProjects/' + project[0] + '.csv')
    csvInput = csv.reader(reader)

    if metricHeaders == []:
        metricHeaders = next(csvInput)

    ProjectFiles = []
    TestFiles = []
    for row in csvInput:
        if row[0] == "File":
            ProjectFiles.append(row)
            if 'test' in row[1].lower():
                TestFiles.append(row)
    
    reader.close()
    
    project.append(ProjectFiles)
    project.append(TestFiles)
    
    #Read Git metrics
    reader = open('UndProjects/' + project[0] + '.log', encoding='utf-8')
    validLine = ['Aut']
    authors = {}
    for line in reader:
        if line[0:3] in validLine:
            author = line[8:line.index('<')-1]
            if author not in authors:
                authors[author] = 0
            authors[author] += 1
        
    reader.close()


    authors = dict(sorted(authors.items(),key=lambda item: item[1],reverse=True))
    project.append(authors)
        
#    print("\nProject: " + project[0])
#    print("Number of .java files: " + str(len(project[1])))
#    print("Number of test files: " + str(len(project[2])))

print('Writing to "UndProjects/AllMetrics.csv"...')
reader = open('UndProjects/AllMetrics.csv','w', newline = '')

csvOutput = csv.writer(reader)
csvOutput.writerow(['Project Name', 'commits', 'contributors', '.java Files', 'LOC', 'test Files', 'test LOC',])

LOCColumn = metricHeaders.index('CountLineCode')

for project in Projects:
    name = project[0]
    totalFiles = len(project[1])
    totalTestFiles = len(project[2])
    LOC = 0
    for file in project[1]:
        LOC += int(file[LOCColumn])
    testLOC = 0
    for file in project[2]:
        testLOC += int(file[LOCColumn])
    commits = sum(project[3].values())
    contributors = len((project[3].keys()))
    
    csvOutput.writerow([name, commits, contributors, totalFiles, LOC, totalTestFiles, testLOC])

reader.close()

print ('-- CSVReader.py Complete --')