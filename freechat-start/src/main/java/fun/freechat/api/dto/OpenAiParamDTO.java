package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "OpenAI series model parameters")
@Data
@EqualsAndHashCode(callSuper = true)
public class OpenAiParamDTO extends AiModelParamDTO{
    @Schema(description = "OpenAI service provider address, default: https://api.openai.com/v1")
    private String baseUrl;
    @Schema(description = "Used to adjust the degree of randomness from sampling in the generated model, the value range is (0, 1.0), a temperature of 0 will always produce the same output. The higher the temperature, the greater the randomness.")
    private Double temperature;
    @Schema(description = "Probability threshold of the nucleus sampling method in the generation process, for example, when the value is 0.8, only the smallest set of most likely tokens whose probabilities add up to 0.8 or more is retained as the candidate set. The value range is (0, 1.0), the larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation.")
    private Double topP;
    @Schema(description = "The maximum number of tokens to generate in the chat completion. The total length of input tokens and generated tokens is limited by the model's context length.")
    private Integer maxTokens;
    @Schema(description = "Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far, increasing the model's likelihood to talk about new topics.")
    private Double presencePenalty;
    @Schema(description = "Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far, decreasing the model's likelihood to repeat the same line verbatim.")
    private Double frequencyPenalty;
    @Schema(description = "If specified, OpenAI will make a best effort to sample deterministically, such that repeated requests with the same seed and parameters should return the same result.")
    private Integer seed;
    @Schema(description = "A collection of stop words that controls the API from generating more tokens.")
    private List<String> stop;
}
