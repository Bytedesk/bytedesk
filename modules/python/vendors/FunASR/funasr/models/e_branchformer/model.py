import logging

from funasr.models.transformer.model import Transformer
from funasr.register import tables


@tables.register("model_classes", "EBranchformer")
class EBranchformer(Transformer):
    """CTC-attention hybrid Encoder-Decoder model"""

    def __init__(
        self,
        *args,
        **kwargs,
    ):

        super().__init__(*args, **kwargs)
