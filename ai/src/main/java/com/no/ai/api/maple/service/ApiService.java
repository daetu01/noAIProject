package com.no.ai.api.maple.service;

import com.no.ai.api.ApiConfig;
import com.no.ai.api.maple.domain.CharacterInfo;
import com.no.ai.api.maple.domain.CharacterStat;
import com.no.ai.api.maple.domain.FinalStat;
import com.no.ai.api.maple.dto.CharacterInfoDto;
import com.no.ai.api.maple.dto.CharacterStatDto;
import com.no.ai.api.maple.repository.CharacterInfoRepository;
import com.no.ai.api.maple.repository.CharacterStatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiService {
    private final CharacterInfoRepository characterInfoRepository;
    private final CharacterStatRepository characterStatRepository;

    private final ApiConfig apiConfig;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    public String getMapleData(String characterName) {
        try {
            String charName = URLEncoder.encode(characterName, StandardCharsets.UTF_8);

            String urlString = apiConfig.getMaple().getBaseUrl() + "/v1/id?character_name=" + charName;
            URL url = new URL(urlString);


            JsonNode jsonNode = objectMapper.readTree(callAPI(url, "GET"));

            String ocid = jsonNode.get("ocid").asText();

            return ocid;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @Transactional
    public CharacterInfoDto.DETAIL getBasicInfo(String ocidCode) {
        try {
            // 기본 정보 갖고 오는 코드
            String basicInfoUrlString = apiConfig.getMaple().getBaseUrl() + "/v1/character/basic?ocid=" + ocidCode;
            URL url = new URL(basicInfoUrlString);
            String resBasicInfo = callAPI(url, "GET");
            System.out.println(resBasicInfo);
            CharacterInfoDto.GET charInfoDto = objectMapper.readValue(resBasicInfo, CharacterInfoDto.GET.class);

            // 인기도 갖고 오는 코드
            String popularityInfoUrlString = apiConfig.getMaple().getBaseUrl() + "/v1/character/popularity?ocid=" + ocidCode;
            url = new URL(popularityInfoUrlString);
            String resPopularity = callAPI(url, "GET");
            JsonNode resJson = objectMapper.readTree(resPopularity);
            int popularity = Integer.parseInt(resJson.get("popularity").asText());

            // 스텟 정보 갖고 오기
            String statInfoUrlString = apiConfig.getMaple().getBaseUrl() + "/v1/character/stat?ocid=" + ocidCode;
            url = new URL(statInfoUrlString);
            String resStat = callAPI(url, "GET");
            System.out.println(resStat);
            CharacterStatDto.GET charStatDto = objectMapper.readValue(resStat, CharacterStatDto.GET.class);

            Optional<CharacterInfo> optionalCharacterInfo =
                    characterInfoRepository.findByCharacterName(charInfoDto.getCharacterName());

            CharacterInfo characterInfo;

            if (optionalCharacterInfo.isEmpty()) {
                characterInfo = modelMapper.map(charInfoDto, CharacterInfo.class);
                characterInfo.setOcidCode(ocidCode);
                characterInfo.setPopularity(popularity);

                characterInfoRepository.save(characterInfo);
            } else {
                characterInfo = optionalCharacterInfo.get();
            }

            System.out.println(characterInfo);
            CharacterStat characterStat = characterStatRepository.findByCharacterInfo(characterInfo)
                    .orElseGet(() -> {
                        CharacterStat newStat = modelMapper.map(charStatDto, CharacterStat.class);
                        newStat.setCharacterInfo(characterInfo);
                        return newStat;
                    });

            characterStat.setCharacterClass(charStatDto.getCharacterClass());
            characterStat.setRemainAp(charStatDto.getRemainAp());

            characterStat.getFinalStats().clear();

            for (CharacterStatDto.FinalStatDto dto : charStatDto.getFinalStat()) {
                FinalStat finalStat = new FinalStat();
                finalStat.setStatName(dto.getStatName());
                finalStat.setStatValue(dto.getStatValue());
                finalStat.setCharacterStat(characterStat);

                characterStat.getFinalStats().add(finalStat);
            }

            characterStatRepository.save(characterStat);

            return CharacterInfoDto.DETAIL.builder()
                    .date(charInfoDto.getDate())
                    .characterName(charInfoDto.getCharacterName())
                    .worldName(charInfoDto.getWorldName())
                    .characterGender(charInfoDto.getCharacterGender())
                    .characterClass(charInfoDto.getCharacterClass())
                    .characterClassLevel(charInfoDto.getCharacterClassLevel())
                    .characterLevel(charInfoDto.getCharacterLevel())
                    .characterExp(charInfoDto.getCharacterExp())
                    .characterExpRate(charInfoDto.getCharacterExpRate())
                    .characterGuildName(charInfoDto.getCharacterGuildName())
                    .characterImage(charInfoDto.getCharacterImage())
                    .characterDateCreate(charInfoDto.getCharacterDateCreate())
                    .accessFlag(charInfoDto.getAccessFlag())
                    .liberationQuestClear(charInfoDto.getLiberationQuestClear())
                    .popularity(popularity)
                    .finalStat(charStatDto.getFinalStat())
                    .remainAp(charStatDto.getRemainAp())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String callAPI(URL url, String method) {
        try {
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("x-nxopen-api-key", apiConfig.getMaple().getKey());

            int responseCode = connection.getResponseCode();

            BufferedReader in;

            if (responseCode == 200) {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();
            while((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();;
        }

        return "";
    }

}
