import { ComponentOptionsMixin } from 'vue';
import { ComponentProvideOptions } from 'vue';
import { DefineComponent } from 'vue';
import { ExtractPropTypes } from 'vue';
import { PublicProps } from 'vue';
import { RendererElement } from 'vue';
import { RendererNode } from 'vue';
import { VNode } from 'vue';

export declare const BytedeskVue: DefineComponent<ExtractPropTypes<    {
locale: {
type: StringConstructor;
default: string;
};
}>, () => VNode<RendererNode, RendererElement, {
[key: string]: any;
}>, {}, {}, {}, ComponentOptionsMixin, ComponentOptionsMixin, "init"[], "init", PublicProps, Readonly<ExtractPropTypes<    {
locale: {
type: StringConstructor;
default: string;
};
}>> & Readonly<{
onInit?: ((...args: any[]) => any) | undefined;
}>, {
locale: string;
}, {}, {}, {}, string, ComponentProvideOptions, true, {}, any>;

export { }
