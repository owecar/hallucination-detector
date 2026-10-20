if (repository.existsById(id)) {

repository.deleteById(id);

redirectAttributes.addFlashAttribute(
"message",
"Question deleted successfully."
            );
        } else 
{
            redirectAttributes.addFlashAttribute(
                "message",
                "Question not found."
            );
        }
        return "redirect:/history";
    }
}
