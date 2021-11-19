import json
import logging
import os
from git import Repo
import git

from py2neo import Graph
from py2neo.cypher import Cursor

from definitions import Neo4jConfigure, OUTPUT_DIR


class Cypher:
    def __init__(self):
        self.graph = None
        self.cypher_str = "MATCH (n:project {language:'$LANGUAGE$'})RETURN n Limit $MAX$"

    def get_libraries(self, language_type_list, extract_max, extract_info_key, save_path, ip=None, username=None,
                      password=None):

        if not ip or username or not password:
            ip = Neo4jConfigure.IP
            username = Neo4jConfigure.USERNAME
            password = Neo4jConfigure.PASSWORD

        if not self.graph:
            self.graph = Graph(ip, auth=(username, password))

        if not os.path.exists(save_path):
            os.mkdir(save_path)

        for language_type in language_type_list:
            cypher_str = self.cypher_str.replace(r"$LANGUAGE$", language_type).replace(r"$MAX$", str(extract_max))

            try:
                result_cursor: Cursor = self.graph.run(cypher_str)
                info_list = []
                for record in result_cursor:
                    node = record[0]
                    res = {key: node[key] for key in extract_info_key}
                    info_list.append(res)

                    js_obj = json.dumps(info_list, indent=4)

                    path_with_name = os.path.join(save_path, language_type) + '.json'
                    with open(path_with_name, 'w') as fileObject:
                        fileObject.write(js_obj)
                        fileObject.close()

            except IndexError:
                logging.warning("cypher {} is execute failed".format(cypher_str))

    def get_url_from_json(self, git_url):
        repo_dir = os.path.join(OUTPUT_DIR, 'repo_dir')
        Repo.clone_from(git_url, repo_dir)


if __name__ == "__main__":
    # 指定提取的语言类型，首字母要大写 e.g.Python
    language_type_list = ["Java","Python"]

    # 指定提取的信息
    extract_info_key = ['repository ID', 'project name', 'repository URL', 'language']

    # 指定最大抽取数量
    extract_max = 2

    # 指定结果保存路径
    save_path = os.path.join(OUTPUT_DIR, 'project')
    # 执行
    cypher = Cypher()

    cypher.get_libraries(language_type_list, extract_max, extract_info_key, save_path)

    # 读取json
    new_list = []
    with open("/Users/imac/project/KGExport/output/project/Java.json", 'r') as fp:
        json_data = json.load(fp)
        for i in json_data:
            url = i['repository URL'].replace('https://github.com', 'https://github.com.cnpmjs.org')
            print(url)
            path_with_name = os.path.join(OUTPUT_DIR, 'repository', i['project name'])
            print(path_with_name)
            Repo.clone_from(url, path_with_name)
