package samples;

import com.interface21.beans.factory.annotation.Autowired;
import com.interface21.context.stereotype.Controller;

@Controller
public class SampleController {

  private SampleService sampleService;

  @Autowired
  public SampleController(final SampleService sampleService) {
    this.sampleService = sampleService;
  }
}
