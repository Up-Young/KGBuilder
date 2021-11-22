
# -*- coding: utf-8 -*-
import json
import logging
import os
from logging import Logger
import subprocess
from git import Repo
from definitions import PROJECT_DIR, REPOSITORIES_DIR, JAR_PATH, JAVA_REPOSITORIES_DIR, REPOSITORIES_PARSER_DIR
from script.get_libraries_from_neo4j import Neo4jQuery
from script.graph_build import graph_build


class KGBuilder:
    # 指定结果保存路径
    def __init__(self):
        self.logger = Logger('KGBuilder')
        handler = logging.StreamHandler()
        self.logger.addHandler(handler)
        self.logger.setLevel(logging.INFO)

    def get_libraries_info(self):
        logging.info("Start getting Library information")

        # 指定提取的语言类型，首字母要大写 e.g.Python
        language_type_list = ["Java", "Python"]
        # 指定提取的信息
        extract_info_key = ['repository ID', 'project name', 'repository URL', 'language',
                            'latest release publish timestamp']
        # 指定最大抽取数量
        extract_max = 10
        # 执行
        cypher = Neo4jQuery()
        cypher.get_libraries(language_type_list, extract_max, extract_info_key, save_path=PROJECT_DIR)
        logging.info("Get Library information has been completed")

    def get_clone_repository(self, language_type, max_num=5):
        logging.info("Start cloning Library repositories")

        # 拼接json路径
        file_path = os.path.join(PROJECT_DIR, language_type) + '.json'
        with open(file_path, 'r') as fp:
            json_data = json.load(fp)
            for i, node in enumerate(json_data):
                if i == max_num:
                    return
                url = node['repository URL'].replace('https://github.com', 'https://github.com.cnpmjs.org')

                path_with_name = os.path.join(REPOSITORIES_DIR, language_type, node['project name'])

                # 如果已经有了这个同名仓库，则跳过
                if os.path.exists(path_with_name):
                    self.logger.warning('Clone repository [{}] already existed, skipping!'.format(node['project name']))
                    continue

                try:
                    Repo.clone_from(url, path_with_name)
                    self.logger.info('Clone repository [{}] finished!'.format(node['project name']))

                    # 因为目前只解析java库，所以删除其他格式的文件，以节约空间
                    # 删除所用非java文件
                    for root, subdirectories, files in os.walk(path_with_name, topdown=False):
                        for file in files:
                            if not file.endswith(".java"):
                                os.remove(os.path.join(root, file))
                    #
                    for root, subdirectories, files in os.walk(path_with_name, topdown=False):
                        if not subdirectories and not files:
                            os.rmdir(root)

                except:
                    self.logger.warning('Clone repository [{}] failed!'.format(node['project name']))

        logging.info("Clone repositories has been completed")

    def repositoriy_parser(self, input_dir=JAVA_REPOSITORIES_DIR, output_dir=REPOSITORIES_PARSER_DIR):
        logging.info("Start parsing Library repositories")
        if not os.path.exists(input_dir):
            logging.error("input_dir {} is not existed".format(JAVA_REPOSITORIES_DIR))
            return

        if not os.path.exists(output_dir):
            os.mkdir(output_dir)

        # 配置运行jar包所需要的参数
        subprocess.call(['java', '-jar', JAR_PATH,
                         '-v',
                         '-h',
                         '-i', input_dir,
                         '-o', output_dir])
        logging.info("Clone repositories has been completed")

    def graph_build(self, doc_source_path=REPOSITORIES_PARSER_DIR):
        logging.info("Start building Library graphdata")
        graph_build("jdk", "v1", )
        logging.info("Build graphdata has been completed")


if __name__ == "__main__":
    kg_builder = KGBuilder()
    # 从指定的neo4j服务中获取第三方库信息，并存为json文件（主要获取仓库名字和链接）
    kg_builder.get_libraries_info()
    #
    # 指定从json文件中提取的语言类型(首字母要大写) 和提取数量，克隆仓库
    kg_builder.get_clone_repository('Java', 5)

    # 对克隆下来的仓库源码进行分析，生成json文件
    kg_builder.repositoriy_parser()

    # 根据解析出来的json建图
    kg_builder.graph_build()
