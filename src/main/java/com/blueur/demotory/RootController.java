package com.blueur.demotory;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class RootController {
  @Autowired
  private HazelcastInstance hazelcastInstance;

  private IMap<String, String> entriesMap() {
    return hazelcastInstance.getMap("entries");
  }

  @GetMapping
  public String index(Model model) {
    final Map<String, String> entries = entriesMap().getAll(entriesMap().keySet());
    model.addAttribute("entries", entries);
    model.addAttribute("entry",
        new Entry(RandomStringUtils.randomAlphanumeric(2), RandomStringUtils.randomAlphanumeric(4)));
    return "index";
  }

  @PostMapping
  public String put(@ModelAttribute Entry entry) {
    entriesMap().put(entry.getKey(), entry.getValue());
    log.info("Put {}={}", entry.getKey(), entry.getValue());
    return "redirect:.";
  }

  @DeleteMapping("/{key}")
  public String delete(@PathVariable String key) {
    entriesMap().delete(key);
    log.info("Delete {}", key);
    return "redirect:.";
  }
}
