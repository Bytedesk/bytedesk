import os
from time import perf_counter
from argparse import ArgumentParser
from fun_text_processing.text_normalization.en.graph_utils import generator_main


def parse_args():
    parser = ArgumentParser()

    parser.add_argument(
        "--language",
        help="language",
        choices=["de", "en", "es", "ru", "zh"],
        default="en",
        type=str,
    )
    parser.add_argument(
        "--input_case",
        help="input capitalization",
        choices=["lower_cased", "cased"],
        default="cased",
        type=str,
    )
    parser.add_argument(
        "--export_dir",
        help="path to export directory. Default to current directory.",
        default="./",
        type=str,
    )
    return parser.parse_args()


def get_grammars(lang: str = "en", input_case: str = "cased"):
    if lang == "de":
        from fun_text_processing.text_normalization.de.taggers.tokenize_and_classify import (
            ClassifyFst,
        )
        from fun_text_processing.text_normalization.de.verbalizers.verbalize_final import (
            VerbalizeFinalFst,
        )
    elif lang == "en":
        from fun_text_processing.text_normalization.en.taggers.tokenize_and_classify import (
            ClassifyFst,
        )
        from fun_text_processing.text_normalization.en.verbalizers.verbalize_final import (
            VerbalizeFinalFst,
        )
    elif lang == "es":
        from fun_text_processing.text_normalization.es.taggers.tokenize_and_classify import (
            ClassifyFst,
        )
        from fun_text_processing.text_normalization.es.verbalizers.verbalize_final import (
            VerbalizeFinalFst,
        )
    elif lang == "ru":
        from fun_text_processing.text_normalization.ru.taggers.tokenize_and_classify import (
            ClassifyFst,
        )
        from fun_text_processing.text_normalization.ru.verbalizers.verbalize_final import (
            VerbalizeFinalFst,
        )
    elif lang == "zh":
        from fun_text_processing.text_normalization.zh.taggers.tokenize_and_classify import (
            ClassifyFst,
        )
        from fun_text_processing.text_normalization.zh.verbalizers.verbalize_final import (
            VerbalizeFinalFst,
        )
    else:
        from fun_text_processing.text_normalization.en.taggers.tokenize_and_classify import (
            ClassifyFst,
        )
        from fun_text_processing.text_normalization.en.verbalizers.verbalize_final import (
            VerbalizeFinalFst,
        )

    return ClassifyFst(input_case=input_case).fst, VerbalizeFinalFst().fst


if __name__ == "__main__":
    args = parse_args()

    export_dir = args.export_dir
    os.makedirs(export_dir, exist_ok=True)
    tagger_far_file = os.path.join(export_dir, args.language + "_tn_tagger.far")
    verbalizer_far_file = os.path.join(export_dir, args.language + "_tn_verbalizer.far")

    start_time = perf_counter()
    tagger_fst, verbalizer_fst = get_grammars(args.language, args.input_case)
    generator_main(tagger_far_file, {"tokenize_and_classify": tagger_fst})
    generator_main(verbalizer_far_file, {"verbalize": verbalizer_fst})
    print(f"Time to generate graph: {round(perf_counter() - start_time, 2)} sec")
