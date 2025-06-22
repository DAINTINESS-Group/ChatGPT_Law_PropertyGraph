package org.uoi.lawtopropertygraphgenerator.api;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uoi.lawtopropertygraphgenerator.config.EnvConfig;
import org.uoi.lawtopropertygraphgenerator.model.law.Entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class GPTApiHandler {

    private static final Logger log = LoggerFactory.getLogger(GPTApiHandler.class);
    private static final String API_URL = EnvConfig.get("GPT_API_URL");
    private static final String API_KEY = EnvConfig.get("GPT_API_KEY");
    private static final String ENTITIES_FILE_PATH = "src/main/resources/output/entities.txt";
    private final JSONArray conversationContext = new JSONArray();
    private final int MAX_RETRIES = 4;
    private final int INITIAL_DELAY = 2000; // 2 seconds
    private final int MAX_DELAY = 10000; // 10 seconds
    private final List<Integer> RETRY_HTTP_STATUS_CODES = List.of(429, 500, 502, 503, 504);

    /**
     * Sends a one-time setup request to store the list of entities in GPT-4o-mini's context.
     */
    public void setupEntitiesAndGiveExample() {
        List<Entity> entities = readEntitiesFile();

        String formattedEntities = entities.stream()
                .map(e -> "- **" + e.getName() + "**: " + e.getDefinition())
                .collect(Collectors.joining("\n"));

        String setupPrompt = String.format("""
               You are acting as a Legal Assistant tasked with analyzing legal texts and extracting relationships between key entities for the purpose of building a Knowledge Graph. Here is a predefined list of key legal entities and their definitions.
               You will use this information to extract relationships from legal text in future messages.
               The list is given below:
               %s
               Acknowledge receipt of this information and await further legal text for processing.
               """, formattedEntities);

        String exampleArticle = """
                Please read the following example article carefully. The article and the following list of relationships serve as examples to guide you on the types of relationships you should identify and the level of detail expected when analyzing similar legal texts. The list of expected relationships below demonstrates the desired format for extraction: (Entity 1) – Relationship Type (e.g., 'is prohibited if') – (Entity 2). These relationship types will be used as labels for the connections in the Knowledge Graph. Pay close attention to the key legal entities involved in these relationships, such as 'AI system', 'person', 'Law enforcement authority', 'Member State', and 'Commission'. You will need to identify and use these entities when extracting relationships from other paragraphs.
                Following this message, you'll receive detailed instructions on precisely how to extract these relationships from a given article.
                **Article:**
                Article 5
                Prohibited AI Practices
                1. The following AI practices shall be prohibited:
                (a) the placing on the market, the putting into service or the use of an AI system that deploys subliminal techniques beyond a person’s consciousness or purposefully manipulative or deceptive techniques, with the objective, or the effect of, materially distorting the behaviour of a person or a group of persons by appreciably impairing their ability to make an informed decision, thereby causing a person to take a decision that that person would not have otherwise taken in a manner that causes or is likely to cause that person, another person or group of persons significant harm;
                (b) the placing on the market, the putting into service or the use of an AI system that exploits any of the vulnerabilities of a person or a specific group of persons due to their age, disability or a specific social or economic situation, with the objective, or the effect, of materially distorting the behaviour of that person or a person belonging to that group in a manner that causes or is reasonably likely to cause that person or another person significant harm;
                (c) the placing on the market, the putting into service or the use of AI systems  for the purpose of the evaluation or classification of natural persons or groups of persons over a certain period of time based on their social behaviour or known, inferred or predicted personal or personality characteristics, with the social score leading to either or both of the following:
                    (i) detrimental or unfavourable treatment of certain natural persons or whole groups of persons in social contexts that are unrelated to the contexts in which the data was originally generated or collected;
                    (ii) detrimental or unfavourable treatment of certain natural persons or  groups of persons that is unjustified or disproportionate to their social behaviour or its gravity;
                (d) the placing on the market, the putting into service for this specific purpose, or the use of an AI system for making risk assessments of natural persons in order to assess or predict the likelihood of a natural person committing a criminal offence, based solely on the profiling of a natural person or on assessing their personality traits and characteristics; this prohibition shall not apply to AI systems used to support the human assessment of the involvement of a person in a criminal activity, which is already based on objective and verifiable facts directly linked to a criminal activity;
                (e) the placing on the market, the putting into service for this specific purpose, or use of AI systems that create or expand facial recognition databases through the untargeted scraping of facial images from the internet or CCTV footage;
                (f) the placing on the market, the putting into service for this specific purpose, or the use of AI systems to infer emotions of a natural person in the areas of workplace and education institutions, except where the use of the AI system is intended to be put in place or into the market for medical or safety reasons.
                (g) the placing on the market, the putting into service for this specific purpose, or the use of biometric categorisation systems that categorise individually natural persons based on their biometric data to deduce or infer their race, political opinions, trade union membership, religious or philosophical beliefs, sex life or sexual orientation; this prohibition does not cover any labelling or filtering of lawfully acquired biometric datasets, such as images, based on biometric data or categorizing of biometric data in the area of law enforcement;
                (h) the use of ‘real-time’ remote biometric identification systems in publicly accessible spaces for the purposes of law enforcement,  unless and in so far as such use is strictly necessary for one of the following objectives:
                    (i) the targeted search for specific  victims of abduction, trafficking in human beings or sexual exploitation of human beings, as well as searching for missing persons;
                    (ii) the prevention of a specific, substantial and imminent threat to the life or physical safety of natural persons or a genuine and present or genuine and foreseeable threat of a terrorist attack;
                    (iii) the  localisation or identification of a person suspected of having committed a criminal offence, for the purpose of conducting a criminal investigation, prosecution or executing a criminal penalty for offences referred to in Annex II and punishable in the Member State concerned by a custodial sentence or a detention order for a maximum period of at least four years;
                Point (h) of the first subparagraph is without prejudice to Article 9 of Regulation (EU) 2016/679 for the processing of biometric data for purposes other than law enforcement.
                2. The use of ‘real-time’ remote biometric identification systems in publicly accessible spaces for the purposes of law enforcement for any of the objectives referred to in paragraph 1, point (h), shall be deployed only for the purposes set out in paragraph 1, point (h), to confirm the identity of the specifically targeted individual, and it shall take into account the following elements:
                (a) the nature of the situation giving rise to the possible use, in particular the seriousness, probability and scale of the harm that would be caused if the system were not used;
                (b) the consequences of the use of the system for the rights and freedoms of all persons concerned, in particular the seriousness, probability and scale of those consequences.
                In addition, the use of ‘real-time’ remote biometric identification systems in publicly accessible spaces for the purposes of law enforcement for any of the objectives referred to in paragraph 1, point (h), of this Article shall comply with necessary and proportionate safeguards and conditions in relation to the use in accordance with national law authorising the use thereof, in particular as regards the temporal, geographic and personal limitations. The use of the ‘real-time’ remote biometric identification system in publicly accessible spaces shall be authorised only if the law enforcement authority has completed a fundamental rights impact assessment as provided for in Article 27 and has registered the system in the EU database according to Article 49. However, in duly justified cases of urgency, the use of such systems may be commenced without the registration in the EU database, provided that such registration is completed without undue delay.
                3. For the purposes of paragraph 1, point (h) and paragraph 2, each  use for the purposes of law enforcement of a ‘real-time’ remote biometric identification system in publicly accessible spaces shall be subject to a prior authorisation granted by a judicial authority or an independent administrative authority whose decision is binding of the Member State in which the use is to take place, issued upon a reasoned request and in accordance with the detailed rules of national law referred to in paragraph 5. However, in a duly justified situation of urgency, the use of such system may be commenced without an authorisation provided that such authorisation is requested without undue delay, at the latest within 24 hours. If such authorisation is rejected, the use shall be stopped with immediate effect and all the data, as well as the results and outputs of that use shall be immediately discarded and deleted.
                The competent judicial authority or an independent administrative authority whose decision is binding shall grant the authorisation only where it is satisfied, on the basis of objective evidence or clear indications presented to it, that the use of the ‘real-time’ remote biometric identification system concerned is necessary for, and proportionate to, achieving one of the objectives specified in paragraph 1, point (h), as identified in the request and, in particular, remains limited to what is strictly necessary concerning the period of time as well as the geographic and personal scope. In deciding on the request, that authority shall take into account the elements referred to in paragraph 2. No decision that produces an adverse legal effect on a person may be taken based solely on the output of the ‘real-time’ remote biometric identification system.
                4. Without prejudice to paragraph 3, each use of a ‘real-time’ remote biometric identification system in publicly accessible spaces for law enforcement purposes shall be notified to the relevant market surveillance authority and the national data protection authority in accordance with the national rules referred to in paragraph 5. The notification shall, as a minimum, contain the information specified under paragraph 6 and shall not include sensitive operational data.
                5. A Member State may decide to provide for the possibility to fully or partially authorise the use of ‘real-time’ remote biometric identification systems in publicly accessible spaces for the purposes of law enforcement within the limits and under the conditions listed in paragraph 1, point (h), and paragraphs 2 and 3.  Member States concerned shall lay down in their national law the necessary detailed rules for the request, issuance and exercise of, as well as supervision and reporting relating to, the authorisations referred to in paragraph 3. Those rules shall also specify in respect of which of the objectives listed in paragraph 1, point (h), including which of the criminal offences referred to in point (h)(iii) thereof, the competent authorities may be authorised to use those systems for the purposes of law enforcement. Member States shall notify those rules to the Commission at the latest 30 days following the adoption thereof. Member States may introduce, in accordance with Union law, more restrictive laws on the use of remote biometric identification systems.
                6. National market surveillance authorities and the national data protection authorities of Member States that have been notified of the use of ‘real-time’ remote biometric identification systems in publicly accessible spaces for law enforcement purposes pursuant to paragraph 4 shall submit to the Commission annual reports on such use. For that purpose, the Commission shall provide Member States and national market surveillance and data protection authorities with a template, including information on the number of the decisions taken by competent judicial authorities or an independent administrative authority whose decision is binding upon requests for authorisations in accordance with paragraph 3 and their result.
                7. The Commission shall publish annual reports on the use of real-time remote biometric identification systems in publicly accessible spaces for law enforcement purposes, based on aggregated data in Member States on the basis of the annual reports referred to in paragraph 6. Those annual reports shall not include sensitive operational data of the related law enforcement activities.
                8. This Article shall not affect the prohibitions that apply where an AI practice infringes other Union law.
                
                **Expected Relationships**
                (Placing on the market of an AI system) – is prohibited if – it deploys subliminal, manipulative, or deceptive techniques causing significant harm or distorting informed decisions
                (Putting into service of an AI system) – is prohibited if – it deploys subliminal, manipulative, or deceptive techniques causing significant harm or distorting informed decisions
                (Use of an AI system) – is prohibited if – it deploys subliminal, manipulative, or deceptive techniques causing significant harm or distorting informed decisions
                (Placing on the market of an AI system) – is prohibited if – it exploits vulnerabilities (due to age, disability, social/economic situation) causing significant harm
                (Putting into service of an AI system) – is prohibited if – it exploits vulnerabilities (due to age, disability, social/economic situation) causing significant harm
                (Use of an AI system) – is prohibited if – it exploits vulnerabilities (due to age, disability, social/economic situation) causing significant harm
                (Placing on the market of an AI system) – is prohibited if – it conducts social scoring leading to detrimental or unjustified/unfavorable treatment
                (Putting into service of an AI system) – is prohibited if – it conducts social scoring leading to detrimental or unjustified/unfavorable treatment
                (Use of an AI system) – is prohibited if – it conducts social scoring leading to detrimental or unjustified/unfavorable treatment
                (Placing on the market of an AI system) – is prohibited if – it predicts criminal offenses solely based on profiling/personality traits
                (Putting into service of an AI system) – is prohibited if – it predicts criminal offenses solely based on profiling/personality traits
                (Use of an AI system) – is prohibited if – it predicts criminal offenses solely based on profiling/personality traits
                (Placing on the market of an AI system) – is prohibited if – it creates or expands facial recognition databases through untargeted scraping
                (Putting into service of an AI system) – is prohibited if – it creates or expands facial recognition databases through untargeted scraping
                (Use of an AI system) – is prohibited if – it creates or expands facial recognition databases through untargeted scraping
                (Placing on the market of an AI system) – is prohibited if – it infers emotions in workplace/education settings (unless for medical/safety reasons)
                (Putting into service of an AI system) – is prohibited if – it infers emotions in workplace/education settings (unless for medical/safety reasons)
                (Use of an AI system) – is prohibited if – it infers emotions in workplace/education settings (unless for medical/safety reasons)
                (Placing on the market of biometric categorisation systems) – is prohibited if – it deduces or infers sensitive attributes (race, political opinions, etc.)
                (Putting into service of biometric categorisation systems) – is prohibited if – it deduces or infers sensitive attributes (race, political opinions, etc.)
                (Use of biometric categorisation systems) – is prohibited if – it deduces or infers sensitive attributes (race, political opinions, etc.)
                (Use of real-time remote biometric identification systems in publicly accessible spaces for law enforcement) – is prohibited unless – strictly necessary for specific objectives (victim search, severe threat prevention, locating serious crime suspects)
                (Law enforcement authority) – must obtain – prior authorization from a judicial or independent authority for each use of real-time remote biometric identification
                (Law enforcement authority) – must notify – national market surveillance authority and national data protection authority before each use of real-time remote biometric identification
                (Judicial or independent administrative authority) – grants or rejects – authorization for real-time remote biometric identification
                (Law enforcement authority) – must discard – data/results if authorization is rejected
                (Member State) – may introduce – more restrictive national rules on remote biometric identification
                (Member State) – must lay down – detailed rules for requesting and supervising authorizations for real-time remote biometric identification
                (Member State) – must notify – the Commission of newly adopted rules on remote biometric identification within 30 days
                (National market surveillance authorities and national data protection authorities) – must submit – annual reports to the Commission on real-time remote biometric identification use
                (The Commission) – shall publish – annual reports on real-time remote biometric identification use based on aggregated Member State data
                **End of Expected Relationships**
                You will then be given other articles from the same Law document, and your task will be to extract relationships from those articles following the examples provided here.
                Following this message, you'll receive detailed instructions on precisely how to extract these relationships from a given article.
                """;

        JSONObject systemMessage = new JSONObject()
                .put("role", "system")
                .put("content", setupPrompt);

        JSONObject userMessage = new JSONObject()
                .put("role", "user")
                .put("content", exampleArticle);

        conversationContext.put(systemMessage);
        conversationContext.put(userMessage);
        conversationContext.put(new JSONObject()
                .put("role", "assistant")
                .put("content", "Acknowledged. Awaiting further legal text for processing."));

        JSONObject payload = new JSONObject();
        payload.put("model", "gpt-4o-mini");
        payload.put("temperature", 0.1);
        payload.put("messages", conversationContext);

        Response response = sendHttpPostRequest(payload);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("GPT API call failed: " + response.getBody().asString());
        }

        String systemContextId = response.body().jsonPath().getString("id");
        log.info("Setup complete, context ID: {}", systemContextId);
    }

    public void passInstructionsToModel() {

        log.info("Passing instructions to model...");

        String instructionsPrompt = """
               These are some instruction on how to extract relationships from a given paragraph.
               After this message you will be given a paragraph to analyze and extract relationships from.
               **Step1: Instructions (Strict GraphML Output Required):**
               - Analyze the given paragraph and extract relationships using the provided legal entities.
               - Extract both **explicit relationships** (directly stated in the text) and **inferred relationships** (logically supported by the text).
               - Prioritize direct, structured legal relationships over inferred ones when there's a conflict.
               - If no relationships exist, return **[]** (empty array) — do not include any text summaries or explanations.
               - **No text summaries, no markdown (`xml`), only raw GraphML script or `[]`.**
               - **Do NOT include ANY xlmns tags or version tags.**
               **Step2: What to Capture:**
               - **Direct legal relationships** (e.g., obligations, responsibilities, prohibitions, requirements) must be extracted.
               - **Inferred relationships** (e.g., implied governance roles, regulatory intent, broader policy goals) are allowed **only if logically supported** by the text.
               - Capture both **structural relationships** (legal definitions, regulatory responsibilities) and **contextual goals** (why the regulation exists).
               - Do not generalize structured legal relationships.
               **Step3: Output Format Requirements (GraphML Format):**
                - The output must be a **valid and complete GraphML** file containing:
               - **Nodes** for each unique entity (`entityA` and `entityB`).
               - **Edges** labeled with `relationshipType` connecting nodes represented by `entityA` and `entityB`.
               - Nodes should have:
                - `id` attribute -> ONLY the entity name from the list of entities.
                - `label` attribute (same as `id`).
               - Edges should have:
                - `source` (entityA).
                - `target` (entityB).
                - `label` (relationshipType) to correctly display relationship meaning. This label should **NOT** exceed **8 words** -> CASE SENSITIVE.
               - **Ensure edges have a `label` attribute** to correctly represent relationships.
                - The output must be **well-formed GraphML** with no missing elements.
                **Your response should be EXACTLY in this format**
                - Example:
               <graphml>
                <graph edgedefault="directed">
                    <node id="Placing on the market of an AI system" label="Placing on the market of an AI system"/>
                    <node id="Corrective action" label="Corrective action"/>
                    <edge source="Placing on the market of an AI system" target="Corrective action" label="shall include" <data>"2.1.1"/>
                </graph>
               </graphml>""";

        JSONObject instructionMessage = new JSONObject()
                .put("role", "system")
                .put("content", instructionsPrompt);

        conversationContext.put(instructionMessage);

        JSONObject payload = new JSONObject();
        payload.put("model", "gpt-4o-mini");
        payload.put("temperature", 0.1);
        payload.put("messages", conversationContext);

        Response response = sendHttpPostRequest(payload);
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("GPT API call failed: " + response.getBody().asString());
        }
        log.info("Instructions passed to model");
    }

    public void extractRelationships(String paragraph, String paragraphID) {

        String graphMLPrompt = String.format("""
        - Use predefined entities only.
        - Provide results strictly in valid GraphML format as instructed before.
        - Nodes **MUST** have attribute: id which is the entity name.
        - Edge **MUST** have attributes: source, target, label, data='%s'.
        
        Paragraph is given below:
        %s
        """, paragraphID, paragraph);


        JSONArray contextForThisParagraph = new JSONArray(conversationContext.toList());
        contextForThisParagraph.put(new JSONObject()
                .put("role", "user")
                .put("content", graphMLPrompt));

        JSONObject payload = new JSONObject();
        payload.put("model", "gpt-4o-mini");
        payload.put("temperature", 0.1);
        payload.put("messages", contextForThisParagraph);

        Response response = sendHttpPostRequest(payload);
        log.info("Sending prompt to GPT-4o-mini for paragraph ID: {}", paragraphID);
        GPTResponseProcessor processor = new GPTResponseProcessor();
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("GPT API call failed: " + response.getBody().asString());
        }

        try {

            String modelReply = response.getBody().jsonPath().getString("choices[0].message.content");
            processor.writeGraphMLToFile(modelReply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Removing reviewed paragraph from conversation context");
        conversationContext.remove(conversationContext.length() - 1);
    }

    /**
     * Reads `entities.txt` and parses it into a list of `Entity` objects.
     */
    protected List<Entity> readEntitiesFile() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(ENTITIES_FILE_PATH));
            return lines.stream()
                    .map(line -> {
                        String[] parts = line.split(" means ", 2);
                        if (parts.length == 2) {
                            return new Entity(parts[0].trim(), parts[1].trim());
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }

    protected Response sendHttpPostRequest(JSONObject payload) {

        // Retry logic for handling rate limits and server errors
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++){
            try {
                Response response = given()
                        .header("Authorization", "Bearer " + API_KEY)
                        .header("Content-Type", "application/json")
                        .body(payload.toString())
                        .post(API_URL);

                if (RETRY_HTTP_STATUS_CODES.contains(response.getStatusCode())){
                    log.warn("Received {} from API on attempt {}/{}. Retrying in {} ms...", response.statusCode(), attempt, MAX_RETRIES, Math.min(INITIAL_DELAY * 2, MAX_DELAY));
                    Thread.sleep(Math.min(INITIAL_DELAY * 2, MAX_DELAY));
                    continue;
                }
                return response;
            } catch (InterruptedException ie) {
                log.error("Request attempt {} failed due to exception: {}", attempt, ie.getMessage());
                throw new RuntimeException("Request failed: " + ie.getMessage());
            }
        }
        throw new RuntimeException("Limit of retries reached. Request failed.");
    }
}