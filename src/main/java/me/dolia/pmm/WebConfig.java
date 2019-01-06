package me.dolia.pmm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Bean
  public TilesConfigurer tilesConfigurer() {
    TilesConfigurer tilesConfigurer = new TilesConfigurer();
    tilesConfigurer.setDefinitions("/WEB-INF/defs/app.xml");
    tilesConfigurer.setCheckRefresh(true);
    return tilesConfigurer;
  }

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    TilesViewResolver viewResolver = new TilesViewResolver();
    registry.viewResolver(viewResolver);
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/").setViewName("index");
  }
}