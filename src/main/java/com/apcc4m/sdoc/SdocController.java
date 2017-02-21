package com.apcc4m.sdoc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.apcc4m.sdoc.bean.Documentation;
import com.apcc4m.sdoc.bean.Options;

@Controller
public class SdocController {

    @Autowired
    private DocumentScanner documentScanner;

    @RequestMapping(value = "/sdoc/api/documents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Documentation> documents(HttpServletRequest req) throws Exception {
        List<Documentation> results = new ArrayList<Documentation>();
        List<Documentation> documentations = documentScanner.getDocumentations();
        for (Documentation item : documentations) {
            Documentation doc = new Documentation();
            doc.setApiInfo(item.getApiInfo());
            doc.setGroupId(item.getGroupId());
            doc.setGroupName(item.getGroupName());
            doc.setTags(item.getTags());
            results.add(doc);
        }
        return results;
    }

    @RequestMapping(value = "/sdoc/api/detail/{groupIndex}/{beanName}/{apiIndex}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Options details(HttpServletRequest req, @PathVariable Integer groupIndex, @PathVariable String beanName,
            @PathVariable int apiIndex) throws Exception {
        Options options = new Options();
        Documentation documentation = documentScanner.getDocumentations().get(groupIndex);
        if (documentation == null) {
            return options;
        }
        List<Options> optionsList = documentation.getOptionsMap().get(beanName);
        if (optionsList != null) {
            options = optionsList.get(apiIndex);
        }
        return options;
    }
}
