import pynini
from fun_text_processing.inverse_text_normalization.fr.graph_utils import (
    DAMO_CHAR,
    GraphFst,
    delete_space,
)
from pynini.lib import pynutil


class MeasureFst(GraphFst):
    """
    Finite state transducer for verbalizing measure, e.g.
        measure { negative: "true" cardinal { integer: "12" } units: "kg" } -> -12 kg

    Args:
        decimal: DecimalFst
        cardinal: CardinalFst
        fraction: FractionFst
    """

    def __init__(self, decimal: GraphFst, cardinal: GraphFst, fraction: GraphFst):
        super().__init__(name="measure", kind="verbalize")
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
            @ decimal.group_by_threes  # measurements most obey three by three spacing
            + delete_space
            + pynutil.delete("}")
        )
        graph_fraction = (
            pynutil.delete("fraction {")
            + delete_space
            + optional_sign
            + delete_space
            + fraction.numbers
            + delete_space
            + pynutil.delete("}")
        )

        graph = (
            (graph_cardinal | graph_decimal | graph_fraction)
            + delete_space
            + pynutil.insert(" ")
            + unit
        )
        delete_tokens = self.delete_tokens(graph)
        self.fst = delete_tokens.optimize()
