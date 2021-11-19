#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
@Author: CandiceYu
@Created: 2021/07/16
"""
import json
from operator import itemgetter
from pathlib import Path
from pprint import pprint

from github import Github
from git import Repo
# from definitions import DATA_DIR, REPO_DIR, REPO_TEST, REPO_ASM, REPO_PAPER


LANGUAGES = ["Python", "Java", "C++", "C#"]


def download_repos(repo_names, github):
    download_repo_cnt = 0

    # record repo_name to url map relation
    with open(Path(DATA_DIR) / "awesome_repo_name2url.txt", 'a+', encoding='utf-8') as fw:
        # record repo_name to language map relation
        with open(Path(DATA_DIR) / "awesome_repo_name2language.txt", 'a+', encoding='utf-8') as fw2:
            # record downloaded repo url
            with open(Path(DATA_DIR) / "awesome_repo_url.txt", 'a+', encoding='utf-8') as fw3:

                for idx, rn in enumerate(repo_names):
                    rn = rn.split("#")[0]

                    url_ = "https://github.com.cnpmjs.org/" + rn
                    print(url_)
                    filePath = REPO_ASM + rn.replace('/', '$$$')
                    if Path(filePath).is_dir():
                        continue

                    # only download python, java, ruby and C++ repos
                    try:
                        repo = github.get_repo(rn)
                        if repo.language in LANGUAGES:
                            download_repo_cnt += 1

                            Repo.clone_from(url=url_, to_path=filePath)
                            # write repo_name to url map relation
                            url_ori = "https://github.com/" + rn
                            fw.write(rn.replace('/', '$$$') + "\t" + url_ori + "\n")
                            # write repo_name to language map relation
                            fw2.write(rn.replace('/', '$$$') + "\t" + repo.language + "\n")
                            # write repo url to txt file
                            fw3.write(url_ori + "\n")

                    except BaseException:
                        print("%s throw BaseException" % rn)

                    print("%d/%d" % (download_repo_cnt, idx + 1))


def download_paper_repo(filePath, github):
    with open(filePath, 'r', encoding='utf-8') as f:
        paper_repos = json.load(f)

    total_repo_cnt = 0
    download_repo_cnt = 0

    # record repo_name to url map relation
    with open(Path(DATA_DIR) / "paper_repo_name2url.txt", 'a+', encoding='utf-8') as fw:
        # record repo_name to language map relation
        with open(Path(DATA_DIR) / "paper_repo_name2language.txt", 'a+', encoding='utf-8') as fw2:

            for paper in paper_repos:
                # url = paper["implementation_url"]
                url = paper
                rn = url.split("github.com/")[1]
                total_repo_cnt += 1

                url_ = "https://github.com.cnpmjs.org/" + rn
                print(url)
                download_path = REPO_PAPER + rn.replace('/', '$$$')
                if Path(download_path).is_dir():
                    continue

                try:
                    repo = github.get_repo(rn)
                    if repo.language in LANGUAGES:
                        download_repo_cnt += 1

                        Repo.clone_from(url=url_, to_path=download_path)

                        # write repo_name to url map relation
                        fw.write(rn.replace('/', '$$$') + "\t" + url + "\n")
                        # write repo_name to language map relation
                        fw2.write(rn.replace('/', '$$$') + "\t" + repo.language + "\n")

                except BaseException:
                    print("%s throw BaseException" % rn)

                print("%d/%d" % (download_repo_cnt, total_repo_cnt))


def repo_language(repo_names, github):
    language_cnt = dict()
    for idx, rn in enumerate(repo_names):
        try:
            repo = github.get_repo(rn)
            language = repo.language
            if language:
                if language not in language_cnt:
                    language_cnt[language] = 0
                language_cnt[language] += 1
        except BaseException:
            print("%s throw BaseException" % rn)

    sorted_dict = sorted(language_cnt.items(), key=itemgetter(1), reverse=True)
    pprint(sorted_dict)


if __name__ == '__main__':
    # github_ = Github("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")
    github_ = Github("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")

    # with open(Path(DATA_DIR) / 'related_repos_from_graph.txt', 'r', encoding='utf-8') as f:
    #     repo_names_ = list(set([line.strip() for line in f.readlines()]))
    # download_repos(repo_names_, github_)
    #
    # with open(Path(DATA_DIR) / 'additional_awesome_repos.txt', 'r', encoding='utf-8') as f:
    #     repo_names_ = list(set([line.strip() for line in f.readlines()]))
    # download_repos(repo_names_, github_)

    # repo_language(repo_names_, github_)

    # repo_names_ = ["huggingface/transformers", "allenai/allennlp", "facebookresearch/detectron2"]
    # download_repos(repo_names_, github_)

    # download_paper_repo(Path(DATA_DIR) / "paper_id2url.json", github_)
    # download_paper_repo(Path(DATA_DIR) / "more_urls.json", github_)
    download_paper_repo(Path(DATA_DIR) / "more_urls_total.json", github_)
