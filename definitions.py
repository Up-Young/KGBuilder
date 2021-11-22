import os

ROOT_DIR = os.path.dirname(os.path.abspath(__file__))  # This is your Project Root

# project data
DATA_DIR = os.path.join(ROOT_DIR, 'data')

# the output dir
OUTPUT_DIR = os.path.join(ROOT_DIR, 'output')
SCRIPT_DIR = os.path.join(ROOT_DIR, 'script')

PROJECT_DIR = os.path.join(DATA_DIR, 'project')
REPOSITORIES_DIR = os.path.join(DATA_DIR, 'repository')
JAVA_REPOSITORIES_DIR = os.path.join(DATA_DIR, 'repository', "Java")

REPOSITORIES_PARSER_DIR = os.path.join(DATA_DIR, 'parseResult')
# jar configuration
JAR_PATH = os.path.join(SCRIPT_DIR, 'APISrcParser-1.0-SNAPSHOT.jar')


class Neo4jConfigure:
    IP = "http://47.116.194.87:9004/browser"
    USERNAME = "neo4j"
    PASSWORD = 'cloudfdse'
