from pathlib import Path
from definitions import  OUTPUT_DIR


class PathUtil:

    @staticmethod
    def graph_data(pro_name, version):
        graph_data_output_dir = Path(OUTPUT_DIR)
        graph_data_output_dir.mkdir(exist_ok=True, parents=True)

        graph_data_path = str(graph_data_output_dir / "{pro}.{version}.graph".format(pro=pro_name, version=version))
        return graph_data_path

    @staticmethod
    def multi_document_collection(pro_name, version):
        doc_output_dir = Path(OUTPUT_DIR)
        doc_output_dir.mkdir(exist_ok=True, parents=True)
        doc_name = doc_output_dir / "{pro}.{v}.dc".format(pro=pro_name, v=version)
        return str(doc_name)
