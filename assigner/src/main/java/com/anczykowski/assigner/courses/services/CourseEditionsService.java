package com.anczykowski.assigner.courses.services;

import com.anczykowski.assigner.courses.models.CourseEdition;
import com.anczykowski.assigner.courses.repositories.CoursesEditionRepository;
import com.anczykowski.assigner.courses.repositories.CoursesRepository;
import com.anczykowski.assigner.error.NotFoundException;
import com.anczykowski.assigner.users.UsersRepository;
import com.anczykowski.assigner.users.UsersService;
import com.anczykowski.assigner.users.models.User;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CourseEditionsService {

    UsersService usersService;

    UsersRepository usersRepository;

    CoursesRepository coursesRepository;

    CoursesEditionRepository coursesEditionRepository;

    private int getHeaderLocation(String[] headers, String columnName) {
        return Arrays.asList(headers).indexOf(columnName);
    }

    private String getUsosIdFromEmail(String email) {
        return StringUtils.substringBefore(email, "@");
    }

    @Transactional
    public CourseEdition create(String courseName, String edition, Reader inputCsvReader) throws IOException {
        var course = coursesRepository.getByName(courseName)
                .orElseThrow(() -> new NotFoundException("%s course not found".formatted(courseName)));
        var courseEdition = CourseEdition.builder()
                .edition(edition)
                .course(course)
                .build();
        var courseEditionSaved = coursesEditionRepository.save(courseEdition);

        final CSVParser parser = new CSVParserBuilder()
                .withSeparator(';')
                .withIgnoreQuotations(true)
                .build();
        try (CSVReader csvReader = new CSVReaderBuilder(inputCsvReader)
                .withCSVParser(parser)
                .build()
        ) {
            String[] line;
            try {
                line = csvReader.readNext();
                var surname_header = getHeaderLocation(line, "nazwisko");
                var first_name_header = getHeaderLocation(line, "imie");
                var second_name_header = getHeaderLocation(line, "imie2");
                var usos_email_header = getHeaderLocation(line, "login_office365");
                while ((line = csvReader.readNext()) != null) {
                    var surname = line[surname_header];
                    var first_name = line[first_name_header];
                    var second_name = line[second_name_header];
                    var usos_email = line[usos_email_header];
                    var usosId = Integer.valueOf(getUsosIdFromEmail(usos_email));
                    var user = User.builder()
                            .name(first_name)
                            .surname(surname)
                            .secondName(second_name.isEmpty() ? null : second_name)
                            .usosId(usosId)
                            .build();
                    var userFetched = usersService.createOrGet(user);
                    userFetched.addCourseEditionAccess(courseEditionSaved);
                    usersRepository.save(userFetched);
                }
            } catch (CsvValidationException e) {
                throw new RuntimeException(e);
            }
        }
        return courseEditionSaved;
    }

    public List<CourseEdition> getAll(String courseName) {
        return coursesEditionRepository.getAll(courseName);
    }

    public CourseEdition get(String courseName, String edition) {
        return coursesEditionRepository.get(courseName, edition)
                .orElseThrow(() -> new NotFoundException("%s %s course edition not found".formatted(courseName, edition)));
    }
}
