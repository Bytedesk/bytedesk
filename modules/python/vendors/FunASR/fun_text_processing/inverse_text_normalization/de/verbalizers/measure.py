import pynini
from fun_text_processing.text_normalization.en.graph_utils import DAMO_CHAR, GraphFst, delete_space
from pynini.lib import pynutil


class MeasureFst(GraphFst):
    """
    Finite state transducer for verbalizing measure, e.g.
        measure { cardinal { negative: "true" integer: "12" } units: "kg" } -> -12 kg
        measure { decimal { integer_part: "1/2" } units: "kg" } -> 1/2 kg
        measure { decimal { integer_part: "1" fractional_part: "2" quantity: "million" } units: "kg" } -> 1,2 million kg

    Args:
        decimal: ITN Decimal verbalizer
        cardinal: ITN Cardinal verbalizer
    """

    def __init__(self, decimal: GraphFst, cardinal: GraphFst, deterministic: bool = True):
        super().__init__(name="measure", kind="verbalize", deterministic=deterministic)
        optional_sign = pynini.closure(pynini.cross('negative: "true"', "-"), 0, 1)
        unit = (
            pynutil.delete("units:")
            + delete_space
            + pynutil.delete('"')
            + pynini.closure(DAMO_CHAR - " ", 1)
            + pynutil.delete('"')
            + delete_space
        )
        graph_decimal = (
            pynutil.delete("decimal {")
            + delete_space
            + optional_sign
            + delete_space
            + decimal.numbers
            + delete_space
            + pynutil.delete("}")
        )
        graph_cardinal = (
            pynutil.delete("cardinal {")
            + delete_space
            + optional_sign
            + delete_space
            + cardinal.numbers
            + delete_space
            + pynutil.delete("}")
        )

        graph = (graph_cardinal | graph_decimal) + delete_space + pynutil.insert(" ") + unit
        delete_tokens = self.delete_tokens(graph)
        self.fst = delete_tokens.optimize()
