package ai.z.openapi.api.layoutparsing;

import ai.z.openapi.service.layoutparsing.LayoutParsingCreateParams;
import ai.z.openapi.service.layoutparsing.LayoutParsingResult;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LayoutParsingApi {

	@POST("layout_parsing")
	Single<LayoutParsingResult> layoutParsing(@Body LayoutParsingCreateParams request);

}
