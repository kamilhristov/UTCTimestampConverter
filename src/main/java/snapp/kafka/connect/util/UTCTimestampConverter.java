package snapp.kafka.connect.util;

import io.debezium.spi.converter.CustomConverter;
import io.debezium.spi.converter.RelationalColumn;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.kafka.connect.data.SchemaBuilder;

public class UTCTimestampConverter implements CustomConverter<SchemaBuilder, RelationalColumn> {

    private static final String UTC_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String LOCAL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private SchemaBuilder timestampSchema = SchemaBuilder.string().name("snapp.time.TimestampString");
    private SimpleDateFormat utcFormatter, localFormatter;

    @Override
    public void configure(Properties props) {

        this.utcFormatter = new SimpleDateFormat(UTC_FORMAT);
        this.utcFormatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        this.localFormatter = new SimpleDateFormat(LOCAL_FORMAT);
        this.localFormatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    }

    @Override
    public void converterFor(RelationalColumn column, ConverterRegistration<SchemaBuilder> registration) {

        if ("DATETIME".equalsIgnoreCase(column.typeName())) {
            registration.register(timestampSchema, value -> {
                
                String localTimestampStr = "1970-01-01 12:00:00";
                
                if (value == null) {
                    if (column.isOptional()){
                        return null;
                    }
                    else if (column.hasDefaultValue()) {
                        return column.defaultValue();
                    }
                }

                try {
                    Date utcDate = this.utcFormatter.parse(value.toString());
                    localTimestampStr = this.localFormatter.format(utcDate);
                } catch (ParseException e) {
                    System.out.println("Exception :" + e);
                }

                return localTimestampStr;
            });
        }
    }
}
