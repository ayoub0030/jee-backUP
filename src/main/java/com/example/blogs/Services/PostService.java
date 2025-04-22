package com.example.blogs.Services;
import com.example.blogs.models.Post;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private static List<Post> posts = new ArrayList<>();
    private static int nextId = 6;

    static {
        posts.add(new Post(1, "مكتب السياسي للأحرار يتمسّك بـمنجزات الحكومة", "عبد العزيز أكرام", "\n" +
                "هسب   ريس\n" +
                "سياسة\n" +
                "المكتب السياسي للأحرار يتمسّك بـ\"منجزات الحكومة\" ويشيد بـ\"مجهودات الوزراء\"\n" +
                "المكتب السياسي للأحرار يتمسّك بـ\"منجزات الحكومة\" ويشيد بـ\"مجهودات الوزراء\"\n" +
                "صور: هسبريس\n" +
                "هسبريس - عبد العزيز أكرام\n" +
                "الأربعاء 9 أبريل 2025 - 23:00\n" +
                "في اجتماعه الشهري المنعقد مساء اليوم الأربعاء، برئاسة عزيز أخنوش، ناقش المكتب السياسي لحزب التجمع الوطني للأحرار مجموعة من الملفات التي تخص العمل الحكومي وحصيلته؛ فضلا عن مجموعة من القضايا الرائجة حاليا داخل المشهد السياسي.\n" +
                "\n" +
                "وأكد أعضاء بالمكتب ذاته أن الاجتماع، الذي تلا اجتماعا لأعضاء فريقي حزب “الحمامة” بالبرلمان، “ناقش تشكيلة من المواضيع الرائجة حاليا؛ بداية بالدعم الأمريكي المتجدد للخيار المغربي من أجل حل النزاع المفتعل حول الصحراء، ثم منجزات المؤسسة التنفيذية وحصيلتها خلال الولاية الحكومية الحالية، إلى جانب موضوع إنشاء مهمة استطلاعية برلمانية حول استيراد المواشي واللحوم، فضلا عن دعم علاقة الحزب بمحيطه المجتمعي”."));
        posts.add(new Post(2, "لترجمة والمنفى بأكاديمية المملكة", "وائل بورشاشن", "ادى الفيلسوف عبد السلام بنعبد العالي، في درس تنصيبه عضوا بأكاديمية المملكة المغربية، باعتماد “فلسفة مغايرة” ترصد وتواجه أشكال “البلاهة”، وتتعقّب الفكر الجاهز في زمن “مجتمع الفُرجة”، كما قدّم رؤية أخرى لمسألة “التعددية”، لا تقتصر على فهمها “تعدّدا في اللسان”.\n" +
                "\n" +
                "وفي درسه بمقر أكاديمية المملكة بالعاصمة الرباط، الأربعاء، عاد بنعبد العالي إلى دروس “الفلسفة والفكر الإسلامي” قائلا: “النقد والمساءلة كانا سَيِّدَي الموقف، مع النصوص الأمهات لتاريخ الفلسفة، التي بقدر ما تلد وتولد من أسئلة، تعمل على توليد الفكر، وكان التمرد على سلطة النصوص”، لكن عند تدريس “الفكر الإسلامي، كان الجميع يشعر أن عليه دخول متحف التاريخ، ونفض الروح النقدية والفكر التساؤلي، بل أغلب الدارسين لم يكونوا يشعرون أن عليهم طرح قضايا فكرية، بل طرح مسائل لا تعنيهم بالفعل، بنظرة قاتلة للفكر، والاحتماء بمنهج تَأْريخي بارد، يستعرض الملل والنحل، وتشعباتها وأصنافها”."));
        posts.add(new Post(3, "تراجع طفيف لأثمنة الخضر", "عماد السنوني", "في سياق متصل، بلغ ثمن البطاطس في حده الأقصى 3,5 دراهم؛ في حين أن البصل لا تزال قيمة الكيلوغرام الواحد منه تلامس 9 دراهم، بينما وصل ثمن القرع الأخضر إلى 7,5 دراهم في الحد الأقصى و4,5 دراهم في الحد الأدنى.\n" +
                "\n" +
                "وإذا كانت أثمنة بعض الخضراوات في حدودها الوسطى، وفق بيانات منصة “الدار البيضاء للخدمات”، فإن أثمنة الفواكه بأصنافها المختلفة لا تزال مرتفعة، حيث بلغ ثمن الكيلوغرام الواحد من الموز المحلي 11,5 دراهم، و18 درهما للمستورد منه؛ في حين أن ثمن البرتقال يلامس 4,5 دراهم للكيلوغرام الواحد و12 درهما بالنسبة للتفاح المُنتج محليا."));
        posts.add(new Post(5, "Le Nigéria s'allie au Maroc pour investir 10 milliards de dollars dans les énergies renouvelables", "Hespress FR", "L'État de Kano, au Nigéria, prévoit d'attirer plus de 10 milliards de dollars d'investissements dans le secteur des énergies renouvelables et des ressources minières solides au cours des cinq prochaines années, grâce à un partenariat stratégique avec plusieurs entreprises marocaines de renom."));

        posts.add(new Post(4, "Angleterre : Le milliardaire marocain Anas Sefrioui envisage de racheter Sheffield Wednesday", "Hespress FR", "Le milliardaire marocain Anas Sefrioui, l'une des plus grandes fortunes du continent africain, envisage de racheter un club de football en Angleterre. Selon les informations rapportées par The Sun, son attention se porte actuellement sur Sheffield Wednesday, club historique évoluant en Championship."));

    }




    public List<Post> getPosts() {
        return new ArrayList<>(posts);
    }

    // New method for pagination
    public List<Post> getPaginatedPosts(int page, int size) {
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, posts.size());

        // Handle invalid indices
        if (startIndex >= posts.size() || startIndex < 0) {
            return new ArrayList<>();
        }

        return posts.subList(startIndex, endIndex);
    }
    public List<Post> searchPosts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getPosts();
        }

        String searchTerm = keyword.toLowerCase().trim();

        return posts.stream()
                .filter(post ->
                        post.getTitle().toLowerCase().contains(searchTerm) ||
                                post.getAuthor().toLowerCase().contains(searchTerm) ||
                                post.getContent().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }


    public List<Post> searchPaginatedPosts(String keyword, int page, int size) {
        List<Post> searchResults = searchPosts(keyword);

        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, searchResults.size());

        // Handle invalid indices
        if (startIndex >= searchResults.size() || startIndex < 0) {
            return new ArrayList<>();
        }

        return searchResults.subList(startIndex, endIndex);
    }
    // Rest of your service methods remain the same
    public Post getPostById(int id) {
        return posts.stream()
                .filter(post -> post.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    public Post createPost(Post post) {
        post.setId(nextId++);
        if (post.getAuthor() == null || post.getAuthor().isEmpty()) {
            post.setAuthor("Anonymous");
        }
        posts.add(post);
        System.out.println("Article créé : " + post.getTitle());
        return post;
    }

    public void updatePost(int id, Post updatedPost) {
        Post existingPost = getPostById(id);
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setAuthor(updatedPost.getAuthor());
        existingPost.setContent(updatedPost.getContent());
        System.out.println("Article mis à jour : " + existingPost.getTitle());
    }

    public void deletePost(int id) {
        Post post = getPostById(id);
        posts.removeIf(p -> p.getId() == id);
        System.out.println("Article supprimé : " + post.getTitle());
    }
}