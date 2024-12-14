import pynini
from fun_text_processing.inverse_text_normalization.zh.utils import get_abs_path
from fun_text_processing.inverse_text_normalization.zh.graph_utils import (
    DAMO_ALNUM,
    DAMO_ALPHA,
    DAMO_DIGIT,
    GraphFst,
    insert_space,
)
from pynini.lib import pynutil


def get_serial_number(cardinal):
    """
    any alphanumerical character sequence with at least one number with length greater equal to 3
    """
    digit = pynini.compose(cardinal.graph_no_exception, DAMO_DIGIT)
    character = digit | DAMO_ALPHA
    sequence = character + pynini.closure(pynutil.delete(" ") + character, 2)
    sequence = sequence @ (pynini.closure(DAMO_ALNUM) + DAMO_DIGIT + pynini.closure(DAMO_ALNUM))
    return sequence.optimize()


class TelephoneFst(GraphFst):
    """
    Finite state transducer for classifying telephone numbers, e.g.
        one two three one two three five six seven eight -> { number_part: "123-123-5678" }

    This class also support card number and IP format.
        "one two three dot one double three dot o dot four o" -> { number_part: "123.133.0.40"}

        "three two double seven three two one four three two one four three double zero five" ->
            { number_part: 3277 3214 3214 3005}

    Args:
        cardinal: CardinalFst
    """

    def __init__(self, cardinal: GraphFst):
        super().__init__(name="telephone", kind="classify")
        # country code, number_part, extension

        graph_digit = pynini.string_file(get_abs_path("data/numbers/digit.tsv"))
        graph_zero = pynini.string_file(get_abs_path("data/numbers/zero.tsv"))
        graph_dot = pynini.string_file(get_abs_path("data/numbers/dot.tsv"))

        graph_digits = graph_digit | graph_zero

        # to handle cases like "one twenty three"
        phone_number_graph_w_country_code = graph_digits**11
        phone_number_graph_wo_country_code = (
            graph_digits**3
            | graph_digits**5
            | graph_digits**11
            | graph_digits**8
            | graph_digits**12
        )

        country_code = (
            pynutil.insert('country_code: "')
            + pynini.closure(pynini.cross("加", "+"), 0, 1)
            + ((pynini.closure(graph_digits, 0, 2) + graph_digits))
            + pynutil.insert('"')
        )

        optional_country_code = pynini.closure(country_code + insert_space, 0, 1).optimize()

        grpah_phone_number_with_country_code = (
            pynutil.insert('number_part: "')
            + pynutil.add_weight(phone_number_graph_w_country_code.optimize(), 0.1)
            + pynutil.insert('"')
        )

        # grpah_phone_number_with_country_code = (
        #    pynutil.insert("number_part: \"")
        #    + phone_number_graph_w_country_code.optimize()
        #    + pynutil.insert("\"")
        # )

        graph_phone_with_country_code = optional_country_code + grpah_phone_number_with_country_code

        grpah_phone_number_wo_country_code = (
            pynutil.insert('number_part: "')
            + phone_number_graph_wo_country_code
            + pynutil.insert('"')
        )

        # phone number
        graph = graph_phone_with_country_code | grpah_phone_number_wo_country_code

        # ip
        ip_graph = graph_digit.plus + (graph_dot + graph_digits.plus).plus

        graph |= pynutil.insert('number_part: "') + ip_graph.optimize() + pynutil.insert('"')
        graph |= (
            pynutil.insert('number_part: "')
            + pynutil.add_weight(get_serial_number(cardinal=cardinal), weight=0.0001)
            + pynutil.insert('"')
        )

        final_graph = self.add_tokens(graph)
        self.fst = final_graph.optimize()
