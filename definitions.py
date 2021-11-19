import os

ROOT_DIR = os.path.dirname(os.path.abspath(__file__))  # This is your Project Root

# project data
DATA_DIR = os.path.join(ROOT_DIR, 'data')

# the output dir
OUTPUT_DIR = os.path.join(ROOT_DIR, 'output')


class Neo4jConfigure:
    IP = "http://47.116.194.87:9004/browser"
    USERNAME = "neo4j"
    PASSWORD = 'cloudfdse'
