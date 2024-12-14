import os

import pynini
from fun_text_processing.inverse_text_normalization.ko.taggers.cardinal import CardinalFst
from fun_text_processing.inverse_text_normalization.ko.taggers.date import DateFst
from fun_text_processing.inverse_text_normalization.ko.taggers.decimal import DecimalFst
from fun_text_processing.inverse_text_normalization.ko.taggers.fraction import FractionFst
from fun_text_processing.inverse_text_normalization.ko.taggers.electronic import ElectronicFst
from fun_text_processing.inverse_text_normalization.ko.taggers.measure import MeasureFst
from fun_text_processing.inverse_text_normalization.ko.taggers.money import MoneyFst
from fun_text_processing.inverse_text_normalization.ko.taggers.punctuation import PunctuationFst
from fun_text_processing.inverse_text_normalization.ko.taggers.telephone import TelephoneFst
from fun_text_processing.inverse_text_normalization.ko.taggers.time import TimeFst
from fun_text_processing.inverse_text_normalization.ko.taggers.whitelist import WhiteListFst
from fun_text_processing.inverse_text_normalization.ko.taggers.word import WordFst
from fun_text_processing.inverse_text_normalization.ko.graph_utils import (
    GraphFst,
    delete_extra_space,
    delete_space,
    generator_main,
)
from pynini.lib import pynutil

import logging


class ClassifyFst(GraphFst):
    """
    Final class that composes all other classification grammars. This class can process an entire sentence, that is lower cased.
    For deployment, this grammar will be compiled and exported to OpenFst Finate State Archiv (FAR) File.
    More details to deployment at NeMo/tools/text_processing_deployment.

    Args:
        cache_dir: path to a dir with .far grammar file. Set to None to avoid using cache.
        overwrite_cache: set to True to overwrite .far files
    """

    def __init__(self, cache_dir: str = None, overwrite_cache: bool = False):
        super().__init__(name="tokenize_and_classify", kind="classify")

        far_file = None
        if cache_dir is not None and cache_dir != "None":
            os.makedirs(cache_dir, exist_ok=True)
            far_file = os.path.join(cache_dir, "_ko_itn.far")
        if not overwrite_cache and far_file and os.path.exists(far_file):
            self.fst = pynini.Far(far_file, mode="r")["tokenize_and_classify"]
            logging.info(f"ClassifyFst.fst was restored from {far_file}.")
        else:
            logging.info(f"Creating ClassifyFst grammars.")
            cardinal = CardinalFst()
            cardinal_graph = cardinal.fst

            decimal = DecimalFst(cardinal)
            decimal_graph = decimal.fst

            fraction = FractionFst(cardinal)
            fraction_graph = fraction.fst

            measure_graph = MeasureFst(cardinal=cardinal, decimal=decimal).fst
            date_graph = DateFst().fst
            word_graph = WordFst().fst
            time_graph = TimeFst().fst
            money_graph = MoneyFst(cardinal=cardinal, decimal=decimal).fst
            whitelist_graph = WhiteListFst().fst
            punct_graph = PunctuationFst().fst
            electronic_graph = ElectronicFst().fst
            telephone_graph = TelephoneFst(cardinal).fst

            classify = (
                pynutil.add_weight(whitelist_graph, 1.01)
                | pynutil.add_weight(time_graph, 1.1)
                | pynutil.add_weight(date_graph, 1.09)
                | pynutil.add_weight(decimal_graph, 1.1)
                | pynutil.add_weight(fraction_graph, 1.1)
                | pynutil.add_weight(measure_graph, 1.1)
                | pynutil.add_weight(cardinal_graph, 1.1)
                | pynutil.add_weight(money_graph, 1.1)
                | pynutil.add_weight(telephone_graph, 1.1)
                | pynutil.add_weight(electronic_graph, 1.1)
                | pynutil.add_weight(word_graph, 100)
            )

            punct = (
                pynutil.insert("tokens { ")
                + pynutil.add_weight(punct_graph, weight=1.1)
                + pynutil.insert(" }")
            )
            token = pynutil.insert("tokens { ") + classify + pynutil.insert(" }")
            token_plus_punct = (
                pynini.closure(punct + pynutil.insert(" "))
                + token
                + pynini.closure(pynutil.insert(" ") + punct)
            )

            graph = token_plus_punct + pynini.closure(delete_extra_space + token_plus_punct)
            graph = delete_space + graph + delete_space

            self.fst = graph.optimize()

            if far_file:
                generator_main(far_file, {"tokenize_and_classify": self.fst})
                logging.info(f"ClassifyFst grammars are saved to {far_file}.")
