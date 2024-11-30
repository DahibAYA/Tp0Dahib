package ma.emsi.tp0dahib;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Backing bean pour la page JSF index.xhtml.
 * Portée view pour conserver l'état de la conversation pendant plusieurs requêtes HTTP.
 */
@Named
@ViewScoped
public class Bb implements Serializable {

    private String systemRole;
    private boolean systemRoleChangeable = true;
    private String question;
    private String reponse;
    private StringBuilder conversation = new StringBuilder();

    @Inject
    private FacesContext facesContext;

    public Bb() {
    }

    public String getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
    }

    public boolean isSystemRoleChangeable() {
        return systemRoleChangeable;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public String getConversation() {
        return conversation.toString();
    }

    public void setConversation(String conversation) {
        this.conversation = new StringBuilder(conversation);
    }

    /**
     * Envoie la question au serveur.
     * Applique un traitement bonus qui transforme la réponse en langage "leet speak".
     *
     * @return null pour rester sur la même page.
     */
    public String envoyer() {
        if (question == null || question.isBlank()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Texte question vide", "Il manque le texte de la question");
            facesContext.addMessage(null, message);
            return null;
        }

        // Si la conversation n'a pas encore commencé, ajouter le rôle système
        if (this.conversation.isEmpty()) {
            this.reponse = systemRole.toUpperCase(Locale.FRENCH) + "\n";
            this.systemRoleChangeable = false;
        } else {
            this.reponse = "";
        }

        // Transformation de la question en "leet speak"
        this.reponse += toLeetSpeak(question);

        afficherConversation();
        return null;
    }

    /**
     * Pour un nouveau chat.
     *
     * @return "index"
     */
    public String nouveauChat() {
        return "index";
    }

    /**
     * Pour afficher la conversation dans le textArea de la page JSF.
     */
    private void afficherConversation() {
        this.conversation.append("== User:\n").append(question).append("\n== Serveur:\n").append(reponse).append("\n");
    }

    /**
     * Transformation du texte en langage "leet speak".
     *
     * @param input Le texte à transformer.
     * @return Le texte transformé.
     */
    private String toLeetSpeak(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (Character.toLowerCase(c)) {
                case 'a': result.append('4'); break;
                case 'e': result.append('3'); break;
                case 'i': result.append('1'); break;
                case 'o': result.append('0'); break;
                case 's': result.append('5'); break;
                case 't': result.append('7'); break;
                default: result.append(c); break;
            }
        }
        return result.toString();
    }

    public List<SelectItem> getSystemRoles() {
        List<SelectItem> listeSystemRoles = new ArrayList<>();
        String role = """
            You are a helpful assistant. You help the user to find the information they need.
            If the user type a question, you answer it.
            """;
        listeSystemRoles.add(new SelectItem(role, "Assistant"));
        role = """
            You are an interpreter. You translate from English to French and from French to English.
            If the user type a French text, you translate it into English.
            If the text contains only one to three words, give some examples of usage of these words in English.
            """;
        listeSystemRoles.add(new SelectItem(role, "Traducteur Anglais-Français"));
        role = """
            Your are a travel guide. If the user type the name of a country or of a town,
            you tell them what are the main places to visit in the country or the town
            and you tell them the average price of a meal.
            """;
        listeSystemRoles.add(new SelectItem(role, "Guide touristique"));
        this.systemRole = (String) listeSystemRoles.get(0).getValue();
        return listeSystemRoles;
    }
}
